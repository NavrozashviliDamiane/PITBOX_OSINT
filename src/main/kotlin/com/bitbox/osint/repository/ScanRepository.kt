package com.bitbox.osint.repository

import com.bitbox.osint.domain.Scan


interface ScanRepository {
    fun save(scan: Scan): Scan
    fun findById(scanId: String): Scan?
    fun findAll(): List<Scan>
    fun delete(scanId: String)
}
