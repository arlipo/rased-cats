package com.rased.server.model

sealed trait ClientDataDto extends Product with Serializable

object ClientDataDto {
  case class Android(
    osData: AndroidAttributes.OsData,
    softwareInfo: AndroidAttributes.SoftwareInfo,
    hardwareInfo: AndroidAttributes.HardwareInfo
  ) extends ClientDataDto

  case class Iphone(
    hardware:              IphoneAttributes.Hardware,
    cellular_info:         IphoneAttributes.CellularInfo,
    local_authentication:  IphoneAttributes.LocalAuthentication,
    operating_system_info: IphoneAttributes.OperatingSystemInfo
  ) extends ClientDataDto
}
