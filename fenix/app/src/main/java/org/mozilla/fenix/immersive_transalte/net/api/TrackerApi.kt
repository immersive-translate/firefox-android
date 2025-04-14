/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.net.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Url

internal interface TrackerApi {

    @POST
    fun appUserBehaviour(
        @Url url: String,
        @Body params: MutableMap<String, Any?>,
    ): Call<Any?>

    @POST
    fun adjustS2sSession(
        @Url url: String,
        @HeaderMap headers: MutableMap<String, Any?>,
        @Body params: MutableMap<String, Any?>,
    ): Call<Any?>

    @POST
    fun adjustS2sEvent(
        @Url url: String,
        @HeaderMap headers: MutableMap<String, Any?>,
        @Body params: MutableMap<String, Any?>,
    ): Call<Any?>
}
