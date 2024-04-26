package com.rased.server.model

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import io.circe.generic.semiauto.deriveCodec

sealed trait UserDataDto extends Product with Serializable

object UserDataDto {
  implicit val customConfig: Configuration =
    Configuration
      .default
      .withDiscriminator("platform")
      .copy(transformConstructorNames = _.toLowerCase)

  implicit val UserDataDtoCodec: Codec[UserDataDto] = deriveConfiguredCodec
  implicit val AndroidCodec: Codec[Android]         = deriveCodec
  implicit val IphoneCodec: Codec[Iphone]           = deriveCodec

  case class Android(
    osData:       AndroidAttributes.OsData,
    softwareInfo: AndroidAttributes.SoftwareInfo,
    hardwareInfo: AndroidAttributes.HardwareInfo
  ) extends UserDataDto

  case class Iphone(
    hardware:              IphoneAttributes.Hardware,
    cellular_info:         IphoneAttributes.CellularInfo,
    local_authentication:  IphoneAttributes.LocalAuthentication,
    operating_system_info: IphoneAttributes.OperatingSystemInfo
  ) extends UserDataDto
}
