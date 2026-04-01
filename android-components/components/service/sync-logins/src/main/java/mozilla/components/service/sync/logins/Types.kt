/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.service.sync.logins

import java.nio.charset.StandardCharsets
import java.util.Base64
import mozilla.components.concept.storage.EncryptedLogin
import mozilla.components.concept.storage.Login
import mozilla.components.concept.storage.LoginEntry
import mozilla.components.concept.storage.ManagedKey
import mozilla.appservices.logins.createManagedEncdec
import mozilla.appservices.logins.createStaticKeyManager

// Convert between application-services data classes and the ones in concept.storage.

/**
 * Encode/decode username/password into concept-storage's encrypted field.
 *
 * AppServices 137+ exposes plaintext [mozilla.appservices.logins.Login] instead of encrypted fields.
 * We keep [EncryptedLogin] API compatibility by storing reversible payload in [EncryptedLogin.secFields].
 */
private fun encodeSecFieldsPlain(username: String, password: String): String {
    val encoder = Base64.getUrlEncoder().withoutPadding()
    val usernameEncoded = encoder.encodeToString(username.toByteArray(StandardCharsets.UTF_8))
    val passwordEncoded = encoder.encodeToString(password.toByteArray(StandardCharsets.UTF_8))
    return "$usernameEncoded:$passwordEncoded"
}

private fun decodeSecFieldsPlain(secFields: String): Pair<String, String> {
    val parts = secFields.split(":", limit = 2)
    if (parts.size != 2) {
        return "" to ""
    }

    return try {
        val decoder = Base64.getUrlDecoder()
        val username = String(decoder.decode(parts[0]), StandardCharsets.UTF_8)
        val password = String(decoder.decode(parts[1]), StandardCharsets.UTF_8)
        username to password
    } catch (_: IllegalArgumentException) {
        "" to ""
    }
}

internal fun encodeSecFields(username: String, password: String, key: ManagedKey): String {
    val keyManager = createStaticKeyManager(key.key)
    val encdec = createManagedEncdec(keyManager)
    val plain = encodeSecFieldsPlain(username, password).toByteArray(StandardCharsets.UTF_8)
    val encrypted = encdec.encrypt(plain)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted)
}

internal fun decodeSecFields(secFields: String, key: ManagedKey): Pair<String, String> {
    return try {
        val encrypted = Base64.getUrlDecoder().decode(secFields)
        val keyManager = createStaticKeyManager(key.key)
        val encdec = createManagedEncdec(keyManager)
        val plain = encdec.decrypt(encrypted)
        decodeSecFieldsPlain(String(plain, StandardCharsets.UTF_8))
    } catch (_: IllegalArgumentException) {
        // Compatibility fallback for non-encrypted payloads produced by intermediate builds.
        decodeSecFieldsPlain(secFields)
    }
}

/**
 * Convert A-S Login into A-C [EncryptedLogin].
 */
fun mozilla.appservices.logins.Login.toEncryptedLogin(key: ManagedKey) = EncryptedLogin(
    guid = id,
    origin = origin,
    formActionOrigin = formActionOrigin?.takeIf { it.isNotEmpty() },
    httpRealm = httpRealm?.takeIf { it.isNotEmpty() },
    usernameField = usernameField,
    passwordField = passwordField,
    timesUsed = timesUsed,
    timeCreated = timeCreated,
    timeLastUsed = timeLastUsed,
    timePasswordChanged = timePasswordChanged,
    secFields = encodeSecFields(username, password, key),
)

/**
 * Convert A-S Login into A-C [Login].
 */
fun mozilla.appservices.logins.Login.toLogin() = Login(
    guid = id,
    origin = origin,
    username = username,
    password = password,
    formActionOrigin = formActionOrigin?.takeIf { it.isNotEmpty() },
    httpRealm = httpRealm?.takeIf { it.isNotEmpty() },
    usernameField = usernameField,
    passwordField = passwordField,
    timesUsed = timesUsed,
    timeCreated = timeCreated,
    timeLastUsed = timeLastUsed,
    timePasswordChanged = timePasswordChanged,
)

/**
 * Convert A-C [LoginEntry] into A-S LoginEntry.
 */
fun LoginEntry.toLoginEntry() = mozilla.appservices.logins.LoginEntry(
    origin = origin,
    httpRealm = httpRealm ?: "",
    formActionOrigin = formActionOrigin ?: "",
    usernameField = usernameField,
    passwordField = passwordField,
    password = password,
    username = username,
)

/**
 * Convert A-C [Login] into A-S Login.
 */
fun Login.toLogin() = mozilla.appservices.logins.Login(
    id = guid,
    timesUsed = timesUsed,
    timeCreated = timeCreated,
    timeLastUsed = timeLastUsed,
    timePasswordChanged = timePasswordChanged,
    origin = origin,
    httpRealm = httpRealm ?: "",
    formActionOrigin = formActionOrigin ?: "",
    usernameField = usernameField,
    passwordField = passwordField,
    password = password,
    username = username,
)
