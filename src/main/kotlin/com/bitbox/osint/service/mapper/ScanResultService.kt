package com.bitbox.osint.service.mapper

import com.bitbox.osint.domain.ScanResult
import com.bitbox.osint.repository.ScanResultRepository
import org.springframework.stereotype.Service

@Service
class ScanResultService(
    private val scanResultRepository: ScanResultRepository
) {
    fun getAllScanResults(): List<ScanResult> {
        return scanResultRepository.findAll()
    }
}
