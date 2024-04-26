package com.rased.server.api

import io.circe.{Decoder, Encoder, HCursor, Json}
import com.rased.server.model._
import endpoints4s.{Codec, generic}

trait ServerRecords extends ServerJsonSchemas with generic.JsonSchemas {

  implicit def IPhoneDataDtoHardwareRecord: Record[IphoneAttributes.Hardware]                       = genericRecord[IphoneAttributes.Hardware]
  implicit def IPhoneDataDtoCellularInfoRecord: Record[IphoneAttributes.CellularInfo]               =
    genericRecord[IphoneAttributes.CellularInfo]
  implicit def IPhoneDataDtoLocalAuthenticationRecord: Record[IphoneAttributes.LocalAuthentication] =
    genericRecord[IphoneAttributes.LocalAuthentication]
  implicit def IPhoneDataDtoOperatingSystemInfoRecord: Record[IphoneAttributes.OperatingSystemInfo] =
    genericRecord[IphoneAttributes.OperatingSystemInfo]
  implicit def IPhoneDataDtoRecord: Record[UserDataDto.Iphone]                                    = genericRecord[UserDataDto.Iphone]

  implicit def AndroidOsDataRecord: Record[AndroidAttributes.OsData]             = genericRecord[AndroidAttributes.OsData]
  implicit def AndroidDisplayInfoRecord: Record[AndroidAttributes.DisplayInfo]   =
    genericRecord[AndroidAttributes.DisplayInfo]
  implicit def AndroidLocaleInfoRecord: Record[AndroidAttributes.LocaleInfo]     =
    genericRecord[AndroidAttributes.LocaleInfo]
  implicit def AndroidHardwareInfoRecord: Record[AndroidAttributes.HardwareInfo] =
    genericRecord[AndroidAttributes.HardwareInfo]
  implicit def AndroidSoftwareInfoRecord: Record[AndroidAttributes.SoftwareInfo] =
    genericRecord[AndroidAttributes.SoftwareInfo]
  implicit def AndroidDtoRecord: Record[UserDataDto.Android]                   = genericRecord[UserDataDto.Android]

  implicit def ClientDataDtoRecord: JsonSchema[UserDataDto] =
    IPhoneDataDtoRecord.orFallbackTo(AndroidDtoRecord)
      .xmap {
        case Left(value)  => value
        case Right(value) => value
      } {
        case value: UserDataDto.Android => Right(value)
        case value: UserDataDto.Iphone  => Left(value)
      }

  implicit def FingerprintResponseRecord: Record[FingerprintResponse] = genericRecord[FingerprintResponse]
}
