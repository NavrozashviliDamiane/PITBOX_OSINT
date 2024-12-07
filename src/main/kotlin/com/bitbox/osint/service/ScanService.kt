package com.ptbox.osint.service


import com.bitbox.osint.dto.ScanRequest
import com.bitbox.osint.dto.ScanResponse


interface ScanService {
    fun initiateScan(scanRequest: ScanRequest): ScanResponse
    fun getScans(): List<ScanResponse>
}
