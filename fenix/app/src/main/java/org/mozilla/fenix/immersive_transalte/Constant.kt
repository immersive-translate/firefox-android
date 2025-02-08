/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte

import org.mozilla.fenix.BuildConfig

object Constant {
    private val isDebug = BuildConfig.DEBUG

    val payKey =
        if (isDebug) "pk_test_51J52ioGc8iUjvqOFgugQR0piFNsl17qiKvUIaqJ4lX4MQCHJ6yxLpwJZ8lQIIWbfJmUEJMk4Mm0dUkSdeCBw2l7g00ht8o181v"
        else "pk_live_51J52ioGc8iUjvqOFjBADyCvVgD7OwQ9219C4v9IzwnQxx3aGarql9cCTRmrDQUvfmIC0rB51KFaYxiMCqfVYhBF6007vIZdu2t"

    val apiBaseUrl = if (isDebug) "https://test-api2.immersivetranslate.com"
    else "https://api2.immersivetranslate.com"

    val workerBaseUrl = if (isDebug) "https://test-worker.immersivetranslate.com"
    else "https://worker.immersivetranslate.com"

    val paySuccess = if (isDebug) "https://test.immersivetranslate.com/accounts/success"
    else "https://immersivetranslate.com/accounts/success"

    val loginPage = if (isDebug) "https://test.immersivetranslate.com/accounts/login"
    else "https://immersivetranslate.com/accounts/login"

    val profile = if (isDebug) "https://test.immersivetranslate.com/profile"
    else "https://immersivetranslate.com/profile"

}
