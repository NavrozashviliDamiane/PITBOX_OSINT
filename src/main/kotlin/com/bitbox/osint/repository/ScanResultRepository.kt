package com.bitbox.osint.repository

import com.bitbox.osint.domain.ScanResult
import java.util.*


interface ScanResultRepository {
    fun save(scanResult: ScanResult): ScanResult
    fun findByScanId(scanId: UUID): ScanResult?
    fun deleteByScanId(scanId: String)
    fun findAll(): List<ScanResult>
}
