/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.onboarding

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import mozilla.components.concept.engine.webextension.InstallationMethod
import mozilla.components.service.nimbus.evalJexlSafe
import mozilla.components.service.nimbus.messaging.use
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import mozilla.components.support.base.ext.areNotificationsEnabledSafe
import mozilla.components.support.base.log.logger.Logger
import mozilla.components.support.utils.BrowsersCache
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.FragmentOnboardingPagesBinding
import org.mozilla.fenix.components.accounts.FenixFxAEntryPoint
import org.mozilla.fenix.components.lazyStore
import org.mozilla.fenix.compose.LinkTextState
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.hideToolbar
import org.mozilla.fenix.ext.isDefaultBrowserPromptSupported
import org.mozilla.fenix.ext.isLargeWindow
import org.mozilla.fenix.ext.nav
import org.mozilla.fenix.ext.openSetDefaultBrowserOption
import org.mozilla.fenix.ext.requireComponents
import org.mozilla.fenix.onboarding.imts.LanguagePageView
import org.mozilla.fenix.onboarding.imts.SecondPageView
import org.mozilla.fenix.onboarding.imts.ThirdPageView
import org.mozilla.fenix.onboarding.imts.ViewPageAdapter
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.nimbus.FxNimbus
import org.mozilla.fenix.onboarding.imts.FourthPageView
import org.mozilla.fenix.onboarding.store.OnboardingAddOnsAction
import org.mozilla.fenix.onboarding.store.OnboardingAddOnsStore
import org.mozilla.fenix.onboarding.store.OnboardingAddonStatus
import org.mozilla.fenix.onboarding.view.Caption
import org.mozilla.fenix.onboarding.view.OnboardingAddOn
import org.mozilla.fenix.onboarding.view.OnboardingPageUiData
import org.mozilla.fenix.onboarding.view.OnboardingScreen
import org.mozilla.fenix.onboarding.view.sequencePosition
import org.mozilla.fenix.onboarding.view.telemetrySequenceId
import org.mozilla.fenix.onboarding.view.toPageUiData
import org.mozilla.fenix.settings.SupportUtils
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.utils.canShowAddSearchWidgetPrompt
import org.mozilla.fenix.utils.showAddSearchWidgetPrompt

/**
 * Fragment displaying the onboarding flow.
 */
class OnboardingFragment : Fragment() {
    private val logger = Logger("OnboardingFragment")

    /*private val pagesToDisplay by lazy {
        pagesToDisplay(
            isNotDefaultBrowser(requireContext()) &&
                activity?.isDefaultBrowserPromptSupported() == false,
            canShowNotificationPage(requireContext()),
            canShowAddSearchWidgetPrompt(),
        )
    }*/
    // private val telemetryRecorder by lazy { OnboardingTelemetryRecorder() }
    //private lateinit var pagesToDisplay: List<OnboardingPageUiData>

    private lateinit var binding: FragmentOnboardingPagesBinding

    private val telemetryRecorder by lazy { OnboardingTelemetryRecorder() }
    private val onboardingAddOnsStore by lazyStore { OnboardingAddOnsStore() }
    private val pinAppWidgetReceiver = WidgetPinnedReceiver()

    private lateinit var viewpagerAdapter: ViewPageAdapter

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = requireContext()
        /*if (pagesToDisplay.isEmpty()) {
            *//* do not continue if there's no onboarding pages to display *//*
            onFinish(null)
        }*/

        if (!isLargeWindow()) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        val filter = IntentFilter(WidgetPinnedReceiver.ACTION)
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(pinAppWidgetReceiver, filter)

        /*if (isNotDefaultBrowser(context) &&
            activity?.isDefaultBrowserPromptSupported() == true
        ) {
            requireComponents.strictMode.resetAfter(StrictMode.allowThreadDiskReads()) {
                promptToSetAsDefaultBrowser()
            }
        }*/

        telemetryRecorder.onOnboardingStarted()
    }

    /*@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setContent {
            FirefoxTheme {
                ScreenContent()
            }
        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOnboardingPagesBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView() {
        val context = requireContext()
        val languagePageView = LanguagePageView(context)
        val secondPageView = SecondPageView(context)
        val thirdPageView = ThirdPageView(context)
        val fourthPageView = FourthPageView(context, activity)

        languagePageView.setCallback(
            object : LanguagePageView.Callback {
                override fun onSelectLang() {
                }

                override fun onSetDefaultBrowser() {
                    activity?.openSetDefaultBrowserOption(useCustomTab = true)
                    binding.viewpager.setCurrentItem(1, true)
                }

                override fun onSkip() {
                    binding.viewpager.setCurrentItem(1, true)
                }
            },
        )

        secondPageView.setCallback(
            object : SecondPageView.Callback {
                override fun onNextClick() {
                    binding.viewpager.setCurrentItem(2, true)
                }
            },
        )

        thirdPageView.setCallback(
            object : ThirdPageView.Callback {
                override fun onFinish() {
                    /*requireComponents.fenixOnboarding.finish()
                    findNavController().nav(
                        id = R.id.onboardingFragment,
                        directions = OnboardingFragmentDirections.actionHome(),
                    )*/
                    binding.viewpager.setCurrentItem(3, true)
                }
            },
        )

        fourthPageView.setCallback(
            object : FourthPageView.Callback {
                override fun onNextClick() {
                    requireComponents.fenixOnboarding.finish()
                    findNavController().nav(
                        id = R.id.onboardingFragment,
                        directions = OnboardingFragmentDirections.actionHome(),
                    )
                }

                override fun onGotoBuy() {
                    requireComponents.fenixOnboarding.finish()
                    findNavController().nav(
                        id = R.id.onboardingFragment,
                        directions = OnboardingFragmentDirections.actionHome(),
                    )
                }
            },
        )

        viewpagerAdapter = ViewPageAdapter(mutableListOf(
            languagePageView, secondPageView,
            thirdPageView, fourthPageView))
        binding.viewpager.offscreenPageLimit = viewpagerAdapter.count
        binding.viewpager.adapter = viewpagerAdapter
        binding.viewpager.addOnPageChangeListener(
            object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {

                }
                override fun onPageSelected(position: Int) {
                    binding.pageIndicator.setIndicatorIndex(position)
                    /*binding.pageIndicator.visibility =
                        if (position != viewpagerAdapter.count - 1) View.GONE
                        else View.INVISIBLE*/
                }
                override fun onPageScrollStateChanged(state: Int) {
                }
            },
        )
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isLargeWindow()) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(pinAppWidgetReceiver)
    }

    /*@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    @Suppress("LongMethod")
    private fun ScreenContent() {
        OnboardingScreen(
            pagesToDisplay = pagesToDisplay,
            onMakeFirefoxDefaultClick = {
                requireComponents.settings.isShownOnBoarding = false
                promptToSetAsDefaultBrowser()
            },
            onSkipDefaultClick = {
                requireComponents.settings.isShownOnBoarding = false
                telemetryRecorder.onSkipSetToDefaultClick(
                    pagesToDisplay.telemetrySequenceId(),
                    pagesToDisplay.sequencePosition(OnboardingPageUiData.Type.DEFAULT_BROWSER),
                )
            },
            onSignInButtonClick = {
                findNavController().nav(
                    id = R.id.onboardingFragment,
                    directions = OnboardingFragmentDirections.actionGlobalTurnOnSync(
                        entrypoint = FenixFxAEntryPoint.NewUserOnboarding,
                    ),
                )
                telemetryRecorder.onSyncSignInClick(
                    sequenceId = pagesToDisplay.telemetrySequenceId(),
                    sequencePosition = pagesToDisplay.sequencePosition(OnboardingPageUiData.Type.SYNC_SIGN_IN),
                )
            },
            onSkipSignInClick = {
                telemetryRecorder.onSkipSignInClick(
                    pagesToDisplay.telemetrySequenceId(),
                    pagesToDisplay.sequencePosition(OnboardingPageUiData.Type.SYNC_SIGN_IN),
                )
            },
            onNotificationPermissionButtonClick = {
                requireComponents.notificationsDelegate.requestNotificationPermission()
                telemetryRecorder.onNotificationPermissionClick(
                    sequenceId = pagesToDisplay.telemetrySequenceId(),
                    sequencePosition =
                    pagesToDisplay.sequencePosition(OnboardingPageUiData.Type.NOTIFICATION_PERMISSION),
                )
            },
            onSkipNotificationClick = {
                telemetryRecorder.onSkipTurnOnNotificationsClick(
                    sequenceId = pagesToDisplay.telemetrySequenceId(),
                    sequencePosition =
                    pagesToDisplay.sequencePosition(OnboardingPageUiData.Type.NOTIFICATION_PERMISSION),
                )
            },
            onAddFirefoxWidgetClick = {
                telemetryRecorder.onAddSearchWidgetClick(
                    pagesToDisplay.telemetrySequenceId(),
                    pagesToDisplay.sequencePosition(OnboardingPageUiData.Type.ADD_SEARCH_WIDGET),
                )
                showAddSearchWidgetPrompt(requireActivity())
            },
            onSkipFirefoxWidgetClick = {
                telemetryRecorder.onSkipAddWidgetClick(
                    pagesToDisplay.telemetrySequenceId(),
                    pagesToDisplay.sequencePosition(OnboardingPageUiData.Type.ADD_SEARCH_WIDGET),
                )
            },
            onAddOnsButtonClick = {
                telemetryRecorder.onAddOnsButtonClick(
                    pagesToDisplay.telemetrySequenceId(),
                    pagesToDisplay.sequencePosition(OnboardingPageUiData.Type.ADD_ONS),
                )
            },
            onFinish = {
                onFinish(it)
                disableNavBarCFRForNewUser()
            },
            onImpression = {
                telemetryRecorder.onImpression(
                    sequenceId = pagesToDisplay.telemetrySequenceId(),
                    pageType = it.type,
                    sequencePosition = pagesToDisplay.sequencePosition(it.type),
                )
            },
            onboardingAddOnsStore = onboardingAddOnsStore,
            onInstallAddOnButtonClick = { installUrl -> installAddon(installUrl) },
        )
    }

    private fun installAddon(addOn: OnboardingAddOn) {
        onboardingAddOnsStore.dispatch(
            OnboardingAddOnsAction.UpdateStatus(
                addOnId = addOn.id,
                status = OnboardingAddonStatus.INSTALLING,
            ),
        )
        requireComponents.addonManager.installAddon(
            url = addOn.installUrl,
            installationMethod = InstallationMethod.ONBOARDING,
            onSuccess = { addon ->
                logger.info("Extension installed successfully")
                telemetryRecorder.onAddOnInstalled(addon.id)
                onboardingAddOnsStore.dispatch(
                    OnboardingAddOnsAction.UpdateStatus(
                        addOnId = addOn.id,
                        status = OnboardingAddonStatus.INSTALLED,
                    ),
                )
            },
            onError = { e ->
                logger.error("Unable to install extension", e)
                onboardingAddOnsStore.dispatch(
                    OnboardingAddOnsAction.UpdateStatus(
                        addOn.id,
                        status = OnboardingAddonStatus.NOT_INSTALLED,
                    ),
                )
            },
        )
    }*/

    /*private fun onFinish(onboardingPageUiData: OnboardingPageUiData?) {
        *//* onboarding page UI data can be null if there was no pages to display *//*
        if (onboardingPageUiData != null) {
            val sequenceId = pagesToDisplay.telemetrySequenceId()
            val sequencePosition = pagesToDisplay.sequencePosition(onboardingPageUiData.type)

            telemetryRecorder.onOnboardingComplete(
                sequenceId = sequenceId,
                sequencePosition = sequencePosition,
            )
        }

        requireComponents.fenixOnboarding.finish()
        findNavController().nav(
            id = R.id.onboardingFragment,
            directions = OnboardingFragmentDirections.actionHome(),
        )
    }*/

    private fun disableNavBarCFRForNewUser() {
        requireContext().settings().shouldShowNavigationBarCFR = false
    }

    // Marked as internal since it is used in unit tests
    internal fun isNotDefaultBrowser(context: Context) =
        !BrowsersCache.all(context.applicationContext).isDefaultBrowser

    private fun canShowNotificationPage(context: Context) =
        !NotificationManagerCompat.from(context.applicationContext)
            .areNotificationsEnabledSafe() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    //private fun isNotATablet() = !resources.getBoolean(R.bool.tablet)

    /*
    private fun pagesToDisplay(
        showDefaultBrowserPage: Boolean,
        showNotificationPage: Boolean,
        showAddWidgetPage: Boolean,
    ): List<OnboardingPageUiData> {
        val jexlConditions = FxNimbus.features.junoOnboarding.value().conditions
        val jexlHelper = requireContext().components.nimbus.createJexlHelper()

        val privacyCaption = Caption(
            text = getString(R.string.juno_onboarding_privacy_notice_text),
            linkTextState = LinkTextState(
                text = getString(R.string.juno_onboarding_privacy_notice_text),
                url = SupportUtils.getMozillaPageUrl(SupportUtils.MozillaPage.PRIVATE_NOTICE),
                onClick = {
                    startActivity(
                        SupportUtils.createSandboxCustomTabIntent(
                            context = requireContext(),
                            url = it,
                        ),
                    )
                    telemetryRecorder.onPrivacyPolicyClick(
                        pagesToDisplay.telemetrySequenceId(),
                        pagesToDisplay.sequencePosition(OnboardingPageUiData.Type.DEFAULT_BROWSER),
                    )
                },
            ),
        )
        return jexlHelper.use {
            FxNimbus.features.junoOnboarding.value().cards.values.toPageUiData(
                privacyCaption,
                showDefaultBrowserPage,
                showNotificationPage,
                showAddWidgetPage,
                jexlConditions,
            ) { condition -> jexlHelper.evalJexlSafe(condition) }
        }
    }

    private fun promptToSetAsDefaultBrowser() {
        activity?.openSetDefaultBrowserOption(useCustomTab = true)
        requireContext().settings().coldStartsBetweenSetAsDefaultPrompts = 0
        requireContext().settings().lastSetAsDefaultPromptShownTimeInMillis = System.currentTimeMillis()
        telemetryRecorder.onSetToDefaultClick(
            sequenceId = pagesToDisplay.telemetrySequenceId(),
            sequencePosition = pagesToDisplay.sequencePosition(OnboardingPageUiData.Type.DEFAULT_BROWSER),
        )
    }*/
}
