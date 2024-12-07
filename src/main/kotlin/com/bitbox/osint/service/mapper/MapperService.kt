package com.bitbox.osint.service.mapper


import com.bitbox.osint.domain.Scan
import com.bitbox.osint.domain.ScanResult
import com.bitbox.osint.dto.ScanResponse
import com.bitbox.osint.dto.ScanResultResponse
import org.springframework.stereotype.Service

@Service
class MapperService {

    fun mapScanToResponse(scan: Scan): ScanResponse {
        return ScanResponse(
            id = scan.id ?: "",
            domain = scan.domain,
            tool = scan.tool,
            status = scan.status,
            startTime = scan.startTime,
            endTime = scan.endTime
        )
    }

    fun mapScanResultToResponse(scanResult: ScanResult): ScanResultResponse {
        return ScanResultResponse(
            scanId = scanResult.scanId,
            emails = scanResult.emails,
            subdomains = scanResult.subdomains,
            ipAddresses = scanResult.ipAddresses,
            hostnames = scanResult.hostnames,
            urls = scanResult.urls,
            banners = scanResult.banners,
            ports = scanResult.ports
        )
    }
}
