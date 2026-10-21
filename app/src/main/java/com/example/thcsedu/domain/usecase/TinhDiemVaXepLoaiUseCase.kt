package com.example.thcsedu.domain.usecase

import com.example.thcsedu.domain.model.XepLoaiEnum
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class TinhDiemVaXepLoaiUseCase @Inject constructor() {

    fun tinhDiemTBMonHK(
        dsDiemTX: List<Float>,
        diemGK: Float?,
        diemCK: Float?
    ): Float? {
        if (diemGK == null || diemCK == null || dsDiemTX.isEmpty()) {
            return null
        }
        val sumTX = dsDiemTX.fold(BigDecimal.ZERO) { acc, score ->
            acc.add(BigDecimal(score.toString()))
        }
        val gkBD = BigDecimal(diemGK.toString())
        val ckBD = BigDecimal(diemCK.toString())

        val total = sumTX
            .add(gkBD.multiply(BigDecimal(2)))
            .add(ckBD.multiply(BigDecimal(3)))
        val count = BigDecimal(dsDiemTX.size + 5)

        return total.divide(count, 10, RoundingMode.HALF_UP)
            .setScale(1, RoundingMode.HALF_UP)
            .toFloat()
    }

    fun xepLoaiHocLuc(
        dsDTBMon: List<Float>,
        dsKetQuaNhanXet: List<Boolean>
    ): XepLoaiEnum {
        val allNhanXetDat = dsKetQuaNhanXet.all { it }

        if (allNhanXetDat && dsDTBMon.all { it >= 6.5f } && dsDTBMon.count { it >= 8.0f } >= 6) {
            return XepLoaiEnum.TOT
        }

        if (allNhanXetDat && dsDTBMon.all { it >= 5.0f } && dsDTBMon.count { it >= 6.5f } >= 6) {
            return XepLoaiEnum.KHA
        }

        val countNhanXetChuaDat = dsKetQuaNhanXet.count { !it }
        if (countNhanXetChuaDat <= 1 && dsDTBMon.count { it >= 5.0f } >= 6 && dsDTBMon.all { it >= 3.5f }) {
            return XepLoaiEnum.DAT
        }

        return XepLoaiEnum.CHUA_DAT
    }
}
