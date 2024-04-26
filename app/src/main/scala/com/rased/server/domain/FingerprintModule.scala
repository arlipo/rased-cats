package com.rased.server.domain

import cats.effect.Sync
import com.rased.server.database.ServerRepository
import com.rased.server.model.Attributes.{Fingerprint, FingerprintString, LastSeen}
import com.rased.server.model.UserDataDto

import scala.util.hashing.MurmurHash3

trait FingerprintModule[F[_]] {
  def calculateFingerprint(data: UserDataDto): F[Fingerprint]

  def updateAndCheckLastSeen(fingerprint: Fingerprint): F[Option[LastSeen]]
}

object FingerprintModule {
  def create[F[_]: Sync](serverRepository: ServerRepository[F]): FingerprintModule[F] = new FingerprintModule[F] {


    def updateAndCheckLastSeen(fingerprint: Fingerprint): F[Option[LastSeen]] =
      serverRepository.upsertFingerprint(fingerprint)

    def calculateFingerprint(data: UserDataDto): F[Fingerprint] = Sync[F].delay {
      val dataList       = getDataList(data)
      val normalisedData = dataList
        .map(_
          .replaceAll("(,|\\s+)", "-")
          .toLowerCase
          .trim)
        .filter(_.nonEmpty)
      val dataStr        = normalisedData.mkString(",")
      val firstHalf      = MurmurHash3.stringHash(dataStr)
      val secondHalf     = MurmurHash3.stringHash(dataStr.reverse)
      val result         = firstHalf.toLong << 32 | (secondHalf & 0xffffffffL)

      Fingerprint(result)
    }

    def getDataList(data: UserDataDto): List[String] = data match {
      case android: UserDataDto.Android =>
        List(
          "android",
          android.osData.gsfId
        )
      case iphone: UserDataDto.Iphone   =>
        List(
          "iphone",
          iphone.hardware.cpu_count,
          iphone.hardware.cpu_frequency,
          iphone.hardware.display_resolution,
          iphone.hardware.device_name,
          iphone.hardware.device_type,
          iphone.hardware.device_model,
          iphone.hardware.display_scale.toString,
          iphone.hardware.kernel_hostname,
          iphone.local_authentication.biometryType,
          iphone.operating_system_info.os_release,
          iphone.operating_system_info.os_build,
          iphone.operating_system_info.os_version,
          iphone.operating_system_info.kernelVersion,
          iphone.operating_system_info.os_type
        )
    }
  }
}
