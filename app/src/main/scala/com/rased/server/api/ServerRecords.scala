package com.rased.server.api

import com.rased.server.model._
import endpoints4s.generic

trait ServerRecords extends ServerJsonSchemas with generic.JsonSchemas {

  implicit def IPhoneDataDtoHardwareRecord: Record[IphoneAttributes.Hardware]                       = genericRecord[IphoneAttributes.Hardware]
  implicit def IPhoneDataDtoCellularInfoRecord: Record[IphoneAttributes.CellularInfo]               =
    genericRecord[IphoneAttributes.CellularInfo]
  implicit def IPhoneDataDtoLocalAuthenticationRecord: Record[IphoneAttributes.LocalAuthentication] =
    genericRecord[IphoneAttributes.LocalAuthentication]
  implicit def IPhoneDataDtoOperatingSystemInfoRecord: Record[IphoneAttributes.OperatingSystemInfo] =
    genericRecord[IphoneAttributes.OperatingSystemInfo]
  implicit def IPhoneDataDtoRecord: Record[ClientDataDto.Iphone]                                    = genericRecord[ClientDataDto.Iphone]





  implicit def AndroidOsDataRecord: Record[AndroidAttributes.OsData] = genericRecord[AndroidAttributes.OsData]
  implicit def AndroidDisplayInfoRecord: Record[AndroidAttributes.DisplayInfo] = genericRecord[AndroidAttributes.DisplayInfo]
  implicit def AndroidLocaleInfoRecord: Record[AndroidAttributes.LocaleInfo] = genericRecord[AndroidAttributes.LocaleInfo]
  implicit def AndroidHardwareInfoRecord: Record[AndroidAttributes.HardwareInfo] = genericRecord[AndroidAttributes.HardwareInfo]
  implicit def AndroidSoftwareInfoRecord: Record[AndroidAttributes.SoftwareInfo] = genericRecord[AndroidAttributes.SoftwareInfo]
  implicit def AndroidDtoRecord: Record[ClientDataDto.Android]       = genericRecord[ClientDataDto.Android]

  implicit def ClientDataDtoRecord: JsonSchema[ClientDataDto] =
    IPhoneDataDtoRecord.orFallbackTo(AndroidDtoRecord)
      .xmap {
        case Left(value)  => value
        case Right(value) => value
      } {
        case value: ClientDataDto.Android => Right(value)
        case value: ClientDataDto.Iphone  => Left(value)
      }

  implicit def FingerprintResponseRecord: Record[FingerprintResponse] = genericRecord[FingerprintResponse]
}
