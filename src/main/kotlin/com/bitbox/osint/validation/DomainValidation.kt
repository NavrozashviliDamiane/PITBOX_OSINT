package com.bitbox.osint.validation

import java.net.InetAddress

class DomainValidation {
    fun validate(domain: String) {
        if (!isValidDomain(domain)) {
            throw IllegalArgumentException("The domain '$domain' does not exist.")
        }
    }

    private fun isValidDomain(domain: String): Boolean {
        return try {
            val address = InetAddress.getByName(domain)
            address.hostAddress.isNotBlank()
        } catch (e: Exception) {
            false
        }
    }
}
