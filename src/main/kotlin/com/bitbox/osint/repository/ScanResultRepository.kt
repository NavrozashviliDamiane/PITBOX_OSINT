package com.bitbox.osint.repository

import com.bitbox.osint.domain.ScanResult


interface ScanResultRepository {
    fun save(scanResult: ScanResult): ScanResult
    fun findByScanId(scanId: String): ScanResult?
    fun deleteByScanId(scanId: String)
    fun findAll(): List<ScanResult>
}
