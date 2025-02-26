/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.browser

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.components.browser.state.selector.findTab
import mozilla.components.browser.state.state.SessionState
import mozilla.components.browser.state.state.TabSessionState
import mozilla.components.browser.thumbnails.BrowserThumbnails
import mozilla.components.browser.toolbar.BrowserToolbar
import mozilla.components.concept.engine.permission.SitePermissions
import mozilla.components.concept.toolbar.Toolbar
import mozilla.components.feature.app.links.AppLinksUseCases
import mozilla.components.feature.contextmenu.ContextMenuCandidate
import mozilla.components.feature.readerview.ReaderViewFeature
import mozilla.components.feature.tab.collections.TabCollection
import mozilla.components.feature.tabs.WindowFeature
import mozilla.components.support.base.feature.UserInteractionHandler
import mozilla.components.support.base.feature.ViewBoundFeatureWrapper
import mozilla.components.support.utils.ext.isLandscape
import mozilla.telemetry.glean.private.NoExtras
import org.mozilla.fenix.GleanMetrics.AddressToolbar
import org.mozilla.fenix.GleanMetrics.ReaderMode
import org.mozilla.fenix.GleanMetrics.Shopping
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.R
import org.mozilla.fenix.browser.tabstrip.isTabStripEnabled
import org.mozilla.fenix.browser.tips.ImmTranslateTipsWindow
import org.mozilla.fenix.browser.tips.ImmTranslateTipsWindow.Type
import org.mozilla.fenix.components.TabCollectionStorage
import org.mozilla.fenix.components.appstate.AppAction.ShoppingAction
import org.mozilla.fenix.components.appstate.AppAction.SnackbarAction
import org.mozilla.fenix.components.toolbar.BrowserToolbarView
import org.mozilla.fenix.components.toolbar.ToolbarMenu
import org.mozilla.fenix.components.toolbar.navbar.shouldAddNavigationBar
import org.mozilla.fenix.components.toolbar.ui.createShareBrowserAction
import org.mozilla.fenix.compose.core.Action
import org.mozilla.fenix.compose.snackbar.Snackbar
import org.mozilla.fenix.compose.snackbar.SnackbarState
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.isLargeWindow
import org.mozilla.fenix.ext.nav
import org.mozilla.fenix.ext.requireComponents
import org.mozilla.fenix.ext.runIfFragmentIsAttached
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.home.HomeFragment
import org.mozilla.fenix.immersive_transalte.JsBridge
import org.mozilla.fenix.immersive_transalte.OnPageCallback
import org.mozilla.fenix.immersive_transalte.UrlLanguageFormater
import org.mozilla.fenix.nimbus.FxNimbus
import org.mozilla.fenix.settings.quicksettings.protections.cookiebanners.getCookieBannerUIMode
import org.mozilla.fenix.shopping.DefaultShoppingExperienceFeature
import org.mozilla.fenix.shopping.ReviewQualityCheckFeature
import org.mozilla.fenix.shortcut.PwaOnboardingObserver
import org.mozilla.fenix.theme.AcornWindowSize
import org.mozilla.fenix.theme.ThemeManager
import org.mozilla.geckoview.GeckoSession

/**
 * Fragment used for browsing the web within the main app.
 */
@Suppress("TooManyFunctions", "LargeClass")
class BrowserFragment : BaseBrowserFragment(), UserInteractionHandler, OnPageCallback {
    private val windowFeature = ViewBoundFeatureWrapper<WindowFeature>()
    private val openInAppOnboardingObserver = ViewBoundFeatureWrapper<OpenInAppOnboardingObserver>()
    private val reviewQualityCheckFeature = ViewBoundFeatureWrapper<ReviewQualityCheckFeature>()
    private val translationsBinding = ViewBoundFeatureWrapper<TranslationsBinding>()

    private var readerModeAvailable = false
    private var reviewQualityCheckAvailable = false
    private var translationsAvailable = false

    private var pwaOnboardingObserver: PwaOnboardingObserver? = null

    @VisibleForTesting
    internal var leadingAction: BrowserToolbar.Button? = null
    private var forwardAction: BrowserToolbar.TwoStateButton? = null
    private var backAction: BrowserToolbar.TwoStateButton? = null
    private var refreshAction: BrowserToolbar.TwoStateButton? = null
    private var immTranslateAction: BrowserToolbar.TwoStateButton? = null
    private var immMenuAction: BrowserToolbar.Button? = null
    private var isTablet: Boolean = false

    override fun onDestroy() {
        JsBridge.removePageStateCallback(this)
        super.onDestroy()
    }

    @Suppress("LongMethod")
    override fun initializeUI(view: View, tab: SessionState) {
        super.initializeUI(view, tab)

        JsBridge.addPageStateCallback(this)
        // default language
        tab.content.url = UrlLanguageFormater.handleUrl(
            requireComponents, tab.content.url)

        val context = requireContext()
        val components = context.components

        if (!context.isTabStripEnabled() && context.settings().isSwipeToolbarToSwitchTabsEnabled) {
            binding.gestureLayout.addGestureListener(
                ToolbarGestureHandler(
                    activity = requireActivity(),
                    contentLayout = binding.browserLayout,
                    tabPreview = binding.tabPreview,
                    toolbarLayout = browserToolbarView.view,
                    store = components.core.store,
                    selectTabUseCase = components.useCases.tabsUseCases.selectTab,
                    onSwipeStarted = {
                        thumbnailsFeature.get()?.requestScreenshot()
                    },
                ),
            )
        }

        updateBrowserToolbarLeadingAndNavigationActions(
            context = context,
            redesignEnabled = context.settings().navigationToolbarEnabled,
            isLandscape = context.isLandscape(),
            isTablet = isLargeWindow(),
            isPrivate = (activity as HomeActivity).browsingModeManager.mode.isPrivate,
            feltPrivateBrowsingEnabled = context.settings().feltPrivateBrowsingEnabled,
            isWindowSizeSmall = AcornWindowSize.getWindowSize(context) == AcornWindowSize.Small,
        )

        updateBrowserToolbarMenuVisibility()

        //initReaderMode(context, view)
        //initTranslationsAction(context, view)
        initReviewQualityCheck(context, view)
        //initSharePageAction(context)
        initImmTranslateAction(context)
        initImmTranslateMenuAction(context)
        initReloadAction(context)

        thumbnailsFeature.set(
            feature = BrowserThumbnails(context, binding.engineView, components.core.store),
            owner = this,
            view = view,
        )

        windowFeature.set(
            feature = WindowFeature(
                store = components.core.store,
                tabsUseCases = components.useCases.tabsUseCases,
            ),
            owner = this,
            view = view,
        )

        if (context.settings().shouldShowOpenInAppCfr) {
            openInAppOnboardingObserver.set(
                feature = OpenInAppOnboardingObserver(
                    context = context,
                    store = context.components.core.store,
                    lifecycleOwner = this,
                    navController = findNavController(),
                    settings = context.settings(),
                    appLinksUseCases = context.components.useCases.appLinksUseCases,
                    container = binding.browserLayout as ViewGroup,
                    shouldScrollWithTopToolbar = !context.settings().shouldUseBottomToolbar,
                ),
                owner = this,
                view = view,
            )
        }
    }

    /**
     * 更新页面 翻译状态
     */
    override fun onPageTranslateStateChange(
        session: GeckoSession,
        pageTranslated: Boolean,
    ) {
        val currentSession = getSafeCurrentTab()?.engineState?.engineSession?.getGeckoSession()
        if (currentSession == null || currentSession != session) {
            return
        }
        if (pageTranslated != isPageTranslated) {
            isPageTranslated = pageTranslated
            if (!isDetached) {
                browserToolbarView.view.invalidateActions()
            }
        }
    }

    private var curTabSessionId: String? = null

    /**
     * tab切换回调
     */
    override fun onTabSelectedChanged(selectedTab: TabSessionState) {
        curTabSessionId = selectedTab.id
        if (selectedTab.content.progress > 0) {
            refreshTranslateState()
        }
    }

    private fun initSharePageAction(context: Context) {
        if (!context.settings().navigationToolbarEnabled || context.isTabStripEnabled()) {
            return
        }

        val sharePageAction = BrowserToolbar.createShareBrowserAction(
            context = context,
        ) {
            AddressToolbar.shareTapped.record((NoExtras()))
            browserToolbarInteractor.onShareActionClicked()
        }

        browserToolbarView.view.addPageAction(sharePageAction)
    }

    private fun initTranslationsAction(context: Context, view: View) {
        if (
            !FxNimbus.features.translations.value().mainFlowToolbarEnabled
        ) {
            return
        }

        val translationsAction = Toolbar.ActionButton(
            AppCompatResources.getDrawable(
                context,
                R.drawable.mozac_ic_translate_24,
            ),
            contentDescription = context.getString(R.string.browser_toolbar_translate),
            iconTintColorResource = ThemeManager.resolveAttribute(R.attr.textPrimary, context),
            visible = { translationsAvailable },
            weight = { TRANSLATIONS_WEIGHT },
            listener = {
                browserToolbarInteractor.onTranslationsButtonClicked()
            },
        )
        browserToolbarView.view.addPageAction(translationsAction)

        translationsBinding.set(
            feature = TranslationsBinding(
                browserStore = context.components.core.store,
                onTranslationsActionUpdated = {
                    translationsAvailable = it.isVisible

                    translationsAction.updateView(
                        tintColorResource = if (it.isTranslated) {
                            R.color.fx_mobile_icon_color_accent_violet
                        } else {
                            ThemeManager.resolveAttribute(R.attr.textPrimary, context)
                        },
                        contentDescription = if (it.isTranslated) {
                            context.getString(
                                R.string.browser_toolbar_translated_successfully,
                                it.fromSelectedLanguage?.localizedDisplayName,
                                it.toSelectedLanguage?.localizedDisplayName,
                            )
                        } else {
                            context.getString(R.string.browser_toolbar_translate)
                        },
                    )

                    safeInvalidateBrowserToolbarView()

                    if (!it.isTranslateProcessing) {
                        requireComponents.appStore.dispatch(SnackbarAction.SnackbarDismissed)
                    }
                },
                onShowTranslationsDialog = browserToolbarInteractor::onTranslationsButtonClicked,
            ),
            owner = this,
            view = view,
        )
    }

    private var isPageTranslated = false
    private var isPageLoading = false
    private val handler = Handler(Looper.getMainLooper())

    /**
     * 翻译按钮
     */
    private fun initImmTranslateAction(context: Context) {
        if (immTranslateAction != null) return

        val isPrivateMode = (activity as HomeActivity).browsingModeManager.mode.isPrivate
        val primaryImage = AppCompatResources.getDrawable(
            context,
            if (!isPrivateMode) R.drawable.ic_imm_trans_home_24
            else R.drawable.ic_imm_trans_private_home_24,
        )
        val secondaryImage = AppCompatResources.getDrawable(
            context,
            if (!isPrivateMode) R.drawable.ic_imm_translated_home_24
            else R.drawable.ic_imm_translated_private_home_24,
        )

        immTranslateAction =
            BrowserToolbar.TwoStateButton(
                primaryImage = primaryImage!!,
                primaryContentDescription = context.getString(R.string.browser_toolbar_imm_trans),
                // primaryImageTintResource = ThemeManager.resolveAttribute(R.attr.textPrimary, context),
                isInPrimaryState = {
                    val isLoading = getSafeCurrentTab()?.content?.loading == true
                    if (!isLoading) {
                        if (isPageLoading) {
                            handler.postDelayed(::refreshTranslateState, 350)
                            showTranslatePopTips()
                        }
                    }
                    if (isPageLoading != isLoading) {
                        isPageLoading = isLoading
                    }

                    !isPageTranslated
                },
                secondaryImage = secondaryImage!!,
                secondaryContentDescription = context.getString(R.string.browser_toolbar_imm_trans),
                disableInSecondaryState = false,
                weight = { TRANSLATIONS_WEIGHT },
                listener = {
                    /*if (getSafeCurrentTab()?.content?.loading == true) {
                        return@TwoStateButton
                    }*/
                    val session = getSafeCurrentTab()?.engineState?.engineSession?.getGeckoSession()
                    session?.let {
                        val geckoSession = it as GeckoSession
                        val jsonObject = JsonObject()
                        val action = if (!isPageTranslated) "translatePage" else "restorePage"
                        JsBridge.callHandler(geckoSession, action, jsonObject) { _ ->
                            /*isPageTranslated = !isPageTranslated
                            browserToolbarView.view.invalidateActions()*/
                            refreshTranslateState()
                        }
                    }
                },
            )

        immTranslateAction?.let {
            browserToolbarView.view.addPageAction(it)
        }
    }

    private val whiteList = mutableListOf(
        "https://immersivetranslate.com/accounts/login",
        "https://test.immersivetranslate.com/accounts/login",
        "https://immersive-translate.cloudflareaccess.com/cdn-cgi/access/login",
    )

    private fun checkWhiteList(url: String): Boolean {
        whiteList.forEach { link ->
            if (url.startsWith(link)) {
                return true
            }
        }
        return false
    }

    private fun showTranslatePopTips() {
        if (isBrowserMenuTipShown) {
            return
        }

        val url = getSafeCurrentTab()?.content?.url
        url?.let {
            if (checkWhiteList(it)) {
                return
            }
        }

        activity?.let { page ->
            if (page.isDestroyed || isDetached || !page.settings().showBrowserMenuTips) {
                return
            }
            ImmTranslateTipsWindow(page, Type.Translate) {
                if (page.isDestroyed || isDetached) {
                    return@ImmTranslateTipsWindow
                }
                ImmTranslateTipsWindow(page, Type.Menu) {
                    isBrowserMenuTipShown = true
                    page.settings().showBrowserMenuTips = false
                }.show(binding.flTipsContainer, binding.swipeRefresh)
            }.show(binding.flTipsContainer, binding.swipeRefresh)
        }
    }

    /**
     * 刷新翻译状态
     */
    private fun refreshTranslateState() {
        val callTabSessionId = getSafeCurrentTab()?.id
        val session = getSafeCurrentTab()?.engineState?.engineSession?.getGeckoSession()
        session?.let {
            val geckoSession = it as GeckoSession
            val jsonObject = JsonObject()
            JsBridge.callHandler(geckoSession, "getPageStatus", jsonObject) { result ->
                try {
                    if (callTabSessionId != curTabSessionId) {
                        return@callHandler
                    }
                    val pageStatus = result.get("pageTranslated").asBoolean
                    if (pageStatus != isPageTranslated) {
                        isPageTranslated = pageStatus
                        browserToolbarView.view.invalidateActions()
                    }
                } finally {
                }
            }
        }
    }

    /**
     * 翻译菜单
     */
    private fun initImmTranslateMenuAction(context: Context) {
        if (immMenuAction != null) return

        immMenuAction = BrowserToolbar.Button(
            imageDrawable = AppCompatResources.getDrawable(
                context,
                R.drawable.ic_imm_menu_home_24,
            )!!,
            contentDescription = context.getString(R.string.browser_toolbar_imm_menu),
            iconTintColorResource = ThemeManager.resolveAttribute(R.attr.textPrimary, context),
            listener = {
                /*if (getSafeCurrentTab()?.content?.loading == true) {
                    return@Button
                }*/
                val session = getSafeCurrentTab()?.engineState?.engineSession?.getGeckoSession()
                session?.let {
                    val geckoSession = it as GeckoSession
                    val jsonObject = JsonObject()
                    JsBridge.callHandler(geckoSession, "openMenu", jsonObject) {}
                }
            },
        )

        immMenuAction?.let {
            browserToolbarView.view.addPageAction(it)
        }
    }

    private fun initReloadAction(context: Context) {
        if (!context.settings().navigationToolbarEnabled) {
            return
        }

        refreshAction =
            BrowserToolbar.TwoStateButton(
                primaryImage = AppCompatResources.getDrawable(
                    context,
                    R.drawable.mozac_ic_arrow_clockwise_24,
                )!!,
                primaryContentDescription = context.getString(R.string.browser_menu_refresh),
                primaryImageTintResource = ThemeManager.resolveAttribute(R.attr.textPrimary, context),
                isInPrimaryState = {
                    getSafeCurrentTab()?.content?.loading == false
                },
                secondaryImage = AppCompatResources.getDrawable(
                    context,
                    R.drawable.mozac_ic_stop,
                )!!,
                secondaryContentDescription = context.getString(R.string.browser_menu_stop),
                disableInSecondaryState = false,
                weight = { RELOAD_WEIGHT },
                longClickListener = {
                    browserToolbarInteractor.onBrowserToolbarMenuItemTapped(
                        ToolbarMenu.Item.Reload(bypassCache = true),
                    )
                },
                listener = {
                    if (getCurrentTab()?.content?.loading == true) {
                        AddressToolbar.cancelTapped.record((NoExtras()))
                        browserToolbarInteractor.onBrowserToolbarMenuItemTapped(ToolbarMenu.Item.Stop)
                    } else {
                        AddressToolbar.reloadTapped.record((NoExtras()))
                        browserToolbarInteractor.onBrowserToolbarMenuItemTapped(
                            ToolbarMenu.Item.Reload(bypassCache = false),
                        )
                    }
                },
            )

        refreshAction?.let {
            browserToolbarView.view.addPageAction(it)
        }
    }

    private fun initReaderMode(context: Context, view: View) {
        val readerModeAction = BrowserToolbar.ToggleButton(
            image = AppCompatResources.getDrawable(
                context,
                R.drawable.ic_readermode,
            )!!,
            imageSelected =
            AppCompatResources.getDrawable(
                context,
                R.drawable.ic_readermode_selected,
            )!!,
            contentDescription = context.getString(R.string.browser_menu_read),
            contentDescriptionSelected = context.getString(R.string.browser_menu_read_close),
            visible = {
                readerModeAvailable && !reviewQualityCheckAvailable
            },
            weight = { READER_MODE_WEIGHT },
            selected = getSafeCurrentTab()?.let {
                activity?.components?.core?.store?.state?.findTab(it.id)?.readerState?.active
            } ?: false,
            listener = browserToolbarInteractor::onReaderModePressed,
        )

        browserToolbarView.view.addPageAction(readerModeAction)

        readerViewFeature.set(
            feature = context.components.strictMode.resetAfter(StrictMode.allowThreadDiskReads()) {
                ReaderViewFeature(
                    context = context,
                    engine = context.components.core.engine,
                    store = context.components.core.store,
                    controlsView = binding.readerViewControlsBar,
                ) { available, active ->
                    if (available) {
                        ReaderMode.available.record(NoExtras())
                    }

                    readerModeAvailable = available
                    readerModeAction.setSelected(active)
                    safeInvalidateBrowserToolbarView()
                }
            },
            owner = this,
            view = view,
        )
    }

    private fun initReviewQualityCheck(context: Context, view: View) {
        val reviewQualityCheck =
            BrowserToolbar.ToggleButton(
                image = AppCompatResources.getDrawable(
                    context,
                    R.drawable.mozac_ic_shopping_24,
                )!!.apply {
                    setTint(ContextCompat.getColor(context, R.color.fx_mobile_text_color_primary))
                },
                imageSelected = AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_shopping_selected,
                )!!,
                contentDescription = context.getString(R.string.review_quality_check_open_handle_content_description),
                contentDescriptionSelected =
                context.getString(R.string.review_quality_check_close_handle_content_description),
                visible = { reviewQualityCheckAvailable },
                weight = { REVIEW_QUALITY_CHECK_WEIGHT },
                listener = { _ ->
                    requireComponents.appStore.dispatch(
                        ShoppingAction.ShoppingSheetStateUpdated(expanded = true),
                    )

                    findNavController().navigate(
                        BrowserFragmentDirections.actionBrowserFragmentToReviewQualityCheckDialogFragment(),
                    )
                    Shopping.addressBarIconClicked.record()
                },
            )

        browserToolbarView.view.addPageAction(reviewQualityCheck)

        reviewQualityCheckFeature.set(
            feature = ReviewQualityCheckFeature(
                appStore = requireComponents.appStore,
                browserStore = context.components.core.store,
                shoppingExperienceFeature = DefaultShoppingExperienceFeature(),
                onIconVisibilityChange = {
                    if (!reviewQualityCheckAvailable && it) {
                        Shopping.addressBarIconDisplayed.record()
                    }
                    reviewQualityCheckAvailable = it
                    safeInvalidateBrowserToolbarView()
                },
                onBottomSheetStateChange = {
                    reviewQualityCheck.setSelected(selected = it, notifyListener = false)
                },
                onProductPageDetected = {
                    Shopping.productPageVisits.add()
                },
            ),
            owner = this,
            view = view,
        )
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun addLeadingAction(
        context: Context,
        showHomeButton: Boolean,
        showEraseButton: Boolean,
    ) {
        if (leadingAction != null) return

        leadingAction = if (showEraseButton) {
            BrowserToolbar.Button(
                imageDrawable = AppCompatResources.getDrawable(
                    context,
                    R.drawable.mozac_ic_data_clearance_24,
                )!!,
                contentDescription = context.getString(R.string.browser_toolbar_erase),
                iconTintColorResource = ThemeManager.resolveAttribute(R.attr.textPrimary, context),
                listener = browserToolbarInteractor::onEraseButtonClicked,
            )
        } else if (showHomeButton) {
            BrowserToolbar.Button(
                imageDrawable = AppCompatResources.getDrawable(
                    context,
                    R.drawable.mozac_ic_home_24,
                )!!,
                contentDescription = context.getString(R.string.browser_toolbar_home),
                iconTintColorResource = ThemeManager.resolveAttribute(R.attr.textPrimary, context),
                listener = browserToolbarInteractor::onHomeButtonClicked,
            )
        } else {
            null
        }

        leadingAction?.let {
            browserToolbarView.view.addNavigationAction(it)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun removeLeadingAction() {
        leadingAction?.let {
            browserToolbarView.view.removeNavigationAction(it)
        }
        leadingAction = null
    }

    /**
     * This code takes care of the [BrowserToolbar] leading and navigation actions.
     * The older design requires a HomeButton followed by navigation buttons for tablets.
     * The newer design expects NavigationButtons and a HomeButton in landscape mode for phones and in both modes
     * for tablets.
     */
    @VisibleForTesting
    internal fun updateBrowserToolbarLeadingAndNavigationActions(
        context: Context,
        redesignEnabled: Boolean,
        isLandscape: Boolean,
        isTablet: Boolean,
        isPrivate: Boolean,
        feltPrivateBrowsingEnabled: Boolean,
        isWindowSizeSmall: Boolean,
    ) {
        if (redesignEnabled) {
            updateAddressBarNavigationActions(
                context = context,
                isWindowSizeSmall = isWindowSizeSmall,
            )
            updateAddressBarLeadingAction(
                redesignEnabled = true,
                isLandscape = isLandscape,
                isTablet = isTablet,
                isPrivate = isPrivate,
                feltPrivateBrowsingEnabled = feltPrivateBrowsingEnabled,
                context = context,
            )
        } else {
            updateAddressBarLeadingAction(
                redesignEnabled = false,
                isLandscape = isLandscape,
                isPrivate = isPrivate,
                isTablet = isTablet,
                feltPrivateBrowsingEnabled = feltPrivateBrowsingEnabled,
                context = context,
            )
            updateTabletToolbarActions(isTablet = isTablet)
        }
        browserToolbarView.view.invalidateActions()
    }

    private fun updateBrowserToolbarMenuVisibility() {
        browserToolbarView.updateMenuVisibility(
            isVisible = !requireContext().shouldAddNavigationBar(),
        )
    }

    @VisibleForTesting
    internal fun updateAddressBarLeadingAction(
        redesignEnabled: Boolean,
        isLandscape: Boolean,
        isTablet: Boolean,
        isPrivate: Boolean,
        feltPrivateBrowsingEnabled: Boolean,
        context: Context,
    ) {
        val showHomeButton = !redesignEnabled
        val showEraseButton = feltPrivateBrowsingEnabled && isPrivate && (isLandscape || isTablet)

        if (showHomeButton || showEraseButton) {
            addLeadingAction(
                context = context,
                showHomeButton = showHomeButton,
                showEraseButton = showEraseButton,
            )
        } else {
            removeLeadingAction()
        }
    }

    @VisibleForTesting
    internal fun updateAddressBarNavigationActions(
        context: Context,
        isWindowSizeSmall: Boolean,
    ) {
        if (!isWindowSizeSmall) {
            addNavigationActions(context)
        } else {
            removeNavigationActions()
        }
    }

    override fun onUpdateToolbarForConfigurationChange(toolbar: BrowserToolbarView) {
        super.onUpdateToolbarForConfigurationChange(toolbar)

        updateBrowserToolbarLeadingAndNavigationActions(
            context = requireContext(),
            redesignEnabled = requireContext().settings().navigationToolbarEnabled,
            isLandscape = requireContext().isLandscape(),
            isTablet = isLargeWindow(),
            isPrivate = (activity as HomeActivity).browsingModeManager.mode.isPrivate,
            feltPrivateBrowsingEnabled = requireContext().settings().feltPrivateBrowsingEnabled,
            isWindowSizeSmall = AcornWindowSize.getWindowSize(requireContext()) == AcornWindowSize.Small,
        )

        updateBrowserToolbarMenuVisibility()
    }

    @VisibleForTesting
    internal fun updateTabletToolbarActions(isTablet: Boolean) {
        if (isTablet == this.isTablet) return

        if (isTablet) {
            addTabletActions(requireContext())
        } else {
            removeTabletActions()
        }

        this.isTablet = isTablet
    }

    @VisibleForTesting
    internal fun addNavigationActions(context: Context) {
        val enableTint = ThemeManager.resolveAttribute(R.attr.textPrimary, context)
        val disableTint = ThemeManager.resolveAttribute(R.attr.textDisabled, context)

        if (backAction == null) {
            backAction = BrowserToolbar.TwoStateButton(
                primaryImage = AppCompatResources.getDrawable(
                    context,
                    R.drawable.mozac_ic_back_24,
                )!!,
                primaryContentDescription = context.getString(R.string.browser_menu_back),
                primaryImageTintResource = enableTint,
                isInPrimaryState = { getSafeCurrentTab()?.content?.canGoBack ?: false },
                secondaryImageTintResource = disableTint,
                disableInSecondaryState = true,
                longClickListener = {
                    browserToolbarInteractor.onBrowserToolbarMenuItemTapped(
                        ToolbarMenu.Item.Back(viewHistory = true),
                    )
                },
                listener = {
                    browserToolbarInteractor.onBrowserToolbarMenuItemTapped(
                        ToolbarMenu.Item.Back(viewHistory = false),
                    )
                },
            ).also {
                browserToolbarView.view.addNavigationAction(it)
            }
        }

        if (forwardAction == null) {
            forwardAction = BrowserToolbar.TwoStateButton(
                primaryImage = AppCompatResources.getDrawable(
                    context,
                    R.drawable.mozac_ic_forward_24,
                )!!,
                primaryContentDescription = context.getString(R.string.browser_menu_forward),
                primaryImageTintResource = enableTint,
                isInPrimaryState = { getSafeCurrentTab()?.content?.canGoForward ?: false },
                secondaryImageTintResource = disableTint,
                disableInSecondaryState = true,
                longClickListener = {
                    browserToolbarInteractor.onBrowserToolbarMenuItemTapped(
                        ToolbarMenu.Item.Forward(viewHistory = true),
                    )
                },
                listener = {
                    browserToolbarInteractor.onBrowserToolbarMenuItemTapped(
                        ToolbarMenu.Item.Forward(viewHistory = false),
                    )
                },
            ).also {
                browserToolbarView.view.addNavigationAction(it)
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun addTabletActions(context: Context) {
        addNavigationActions(context)
        initImmTranslateAction(context)
        initImmTranslateMenuAction(context)
        val enableTint = ThemeManager.resolveAttribute(R.attr.textPrimary, context)
        if (refreshAction == null) {
            refreshAction = BrowserToolbar.TwoStateButton(
                primaryImage = AppCompatResources.getDrawable(
                    context,
                    R.drawable.mozac_ic_arrow_clockwise_24,
                )!!,
                primaryContentDescription = context.getString(R.string.browser_menu_refresh),
                primaryImageTintResource = enableTint,
                isInPrimaryState = {
                    getSafeCurrentTab()?.content?.loading == false
                },
                secondaryImage = AppCompatResources.getDrawable(
                    context,
                    R.drawable.mozac_ic_stop,
                )!!,
                secondaryContentDescription = context.getString(R.string.browser_menu_stop),
                disableInSecondaryState = false,
                longClickListener = {
                    browserToolbarInteractor.onBrowserToolbarMenuItemTapped(
                        ToolbarMenu.Item.Reload(bypassCache = true),
                    )
                },
                listener = {
                    if (getCurrentTab()?.content?.loading == true) {
                        browserToolbarInteractor.onBrowserToolbarMenuItemTapped(ToolbarMenu.Item.Stop)
                    } else {
                        browserToolbarInteractor.onBrowserToolbarMenuItemTapped(
                            ToolbarMenu.Item.Reload(bypassCache = false),
                        )
                    }
                },
            ).also {
                browserToolbarView.view.addNavigationAction(it)
            }
        }
    }

    @VisibleForTesting
    internal fun removeNavigationActions() {
        forwardAction?.let {
            browserToolbarView.view.removeNavigationAction(it)
        }
        forwardAction = null
        backAction?.let {
            browserToolbarView.view.removeNavigationAction(it)
        }
        backAction = null
    }

    @VisibleForTesting
    internal fun removeTabletActions() {
        removeNavigationActions()

        refreshAction?.let {
            browserToolbarView.view.removeNavigationAction(it)
        }
        immTranslateAction?.let {
            browserToolbarView.view.removeNavigationAction(it)
        }
        immMenuAction?.let {
            browserToolbarView.view.removeNavigationAction(it)
        }
    }

    override fun onStart() {
        super.onStart()
        val context = requireContext()
        val settings = context.settings()

        if (!settings.userKnowsAboutPwas) {
            pwaOnboardingObserver = PwaOnboardingObserver(
                store = context.components.core.store,
                lifecycleOwner = this,
                navController = findNavController(),
                settings = settings,
                webAppUseCases = context.components.useCases.webAppUseCases,
            ).also {
                it.start()
            }
        }

        subscribeToTabCollections()
        updateLastBrowseActivity()
    }

    override fun onStop() {
        super.onStop()
        updateLastBrowseActivity()
        updateHistoryMetadata()
        pwaOnboardingObserver?.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isTablet = false
        leadingAction = null
        forwardAction = null
        backAction = null
        refreshAction = null
        immMenuAction = null
        immTranslateAction = null
    }

    private fun updateHistoryMetadata() {
        getCurrentTab()?.let { tab ->
            (tab as? TabSessionState)?.historyMetadata?.let {
                requireComponents.core.historyMetadataService.updateMetadata(it, tab)
            }
        }
    }

    private fun subscribeToTabCollections() {
        Observer<List<TabCollection>> {
            requireComponents.core.tabCollectionStorage.cachedTabCollections = it
        }.also { observer ->
            requireComponents.core.tabCollectionStorage.getCollections()
                .observe(viewLifecycleOwner, observer)
        }
    }

    override fun onResume() {
        super.onResume()
        requireComponents.core.tabCollectionStorage.register(collectionStorageObserver, this)
    }

    override fun onBackPressed(): Boolean {
        return readerViewFeature.onBackPressed() || super.onBackPressed()
    }

    override fun navToQuickSettingsSheet(tab: SessionState, sitePermissions: SitePermissions?) {
        val useCase = requireComponents.useCases.trackingProtectionUseCases
        FxNimbus.features.cookieBanners.recordExposure()
        useCase.containsException(tab.id) { hasTrackingProtectionException ->
            lifecycleScope.launch {
                val cookieBannersStorage = requireComponents.core.cookieBannersStorage
                val cookieBannerUIMode = cookieBannersStorage.getCookieBannerUIMode(
                    requireContext(),
                    tab,
                )
                withContext(Dispatchers.Main) {
                    runIfFragmentIsAttached {
                        val isTrackingProtectionEnabled =
                            tab.trackingProtection.enabled && !hasTrackingProtectionException
                        val directions =
                            BrowserFragmentDirections.actionBrowserFragmentToQuickSettingsSheetDialogFragment(
                                sessionId = tab.id,
                                url = tab.content.url,
                                title = tab.content.title,
                                isSecured = tab.content.securityInfo.secure,
                                sitePermissions = sitePermissions,
                                gravity = getAppropriateLayoutGravity(),
                                certificateName = tab.content.securityInfo.issuer,
                                permissionHighlights = tab.content.permissionHighlights,
                                isTrackingProtectionEnabled = isTrackingProtectionEnabled,
                                cookieBannerUIMode = cookieBannerUIMode,
                            )
                        nav(R.id.browserFragment, directions)
                    }
                }
            }
        }
    }

    private val collectionStorageObserver = object : TabCollectionStorage.Observer {
        override fun onCollectionCreated(
            title: String,
            sessions: List<TabSessionState>,
            id: Long?,
        ) {
            showTabSavedToCollectionSnackbar(sessions.size, true)
        }

        override fun onTabsAdded(tabCollection: TabCollection, sessions: List<TabSessionState>) {
            showTabSavedToCollectionSnackbar(sessions.size)
        }

        private fun showTabSavedToCollectionSnackbar(
            tabSize: Int,
            isNewCollection: Boolean = false,
        ) {
            view?.let {
                val messageStringRes = when {
                    isNewCollection -> {
                        R.string.create_collection_tabs_saved_new_collection
                    }
                    tabSize > 1 -> {
                        R.string.create_collection_tabs_saved
                    }
                    else -> {
                        R.string.create_collection_tab_saved
                    }
                }
                Snackbar.make(
                    snackBarParentView = binding.dynamicSnackbarContainer,
                    snackbarState = SnackbarState(
                        message = getString(messageStringRes),
                        action = Action(
                            label = getString(R.string.create_collection_view),
                            onClick = {
                                findNavController().navigate(
                                    BrowserFragmentDirections.actionGlobalHome(
                                        focusOnAddressBar = false,
                                        scrollToCollection = true,
                                    ),
                                )
                            },
                        ),
                    ),
                ).show()
            }
        }
    }

    override fun getContextMenuCandidates(
        context: Context,
        view: View,
    ): List<ContextMenuCandidate> {
        val contextMenuCandidateAppLinksUseCases = AppLinksUseCases(
            requireContext(),
            { true },
        )

        return ContextMenuCandidate.defaultCandidates(
            context,
            context.components.useCases.tabsUseCases,
            context.components.useCases.contextMenuUseCases,
            view,
            ContextMenuSnackbarDelegate(),
        ) + ContextMenuCandidate.createOpenInExternalAppCandidate(
            requireContext(),
            contextMenuCandidateAppLinksUseCases,
        )
    }

    /**
     * Updates the last time the user was active on the [BrowserFragment].
     * This is useful to determine if the user has to start on the [HomeFragment]
     * or it should go directly to the [BrowserFragment].
     */
    @VisibleForTesting
    internal fun updateLastBrowseActivity() {
        requireContext().settings().lastBrowseActivity = System.currentTimeMillis()
    }

    companion object {
        /**
         * Indicates weight of a page action. The lesser the weight, the closer it is to the URL.
         *
         * A weight of -1 indicates the position is not cared for and the action will be appended at the end.
         */
        const val READER_MODE_WEIGHT = 1
        const val TRANSLATIONS_WEIGHT = 2
        const val REVIEW_QUALITY_CHECK_WEIGHT = 3
        const val SHARE_WEIGHT = 4
        const val RELOAD_WEIGHT = 5
        const val OPEN_IN_ACTION_WEIGHT = 6

        var isBrowserMenuTipShown = false
    }
}
