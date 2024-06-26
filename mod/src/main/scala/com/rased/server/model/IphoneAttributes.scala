package com.rased.server.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

object IphoneAttributes {
  case class Hardware(
    cpu_count:          String,
    cpu_frequency:      String,
    display_resolution: String,
    physical_memory:    String, // exclude
    device_name:        String,
    device_type:        String,
    device_model:       String,
    display_scale:      Double,
    memory_size:        String, // exclude
    kernel_hostname:    String
  )

  case class CellularInfo(
    wifiAddress: String // exclude
  )

  case class LocalAuthentication(
    isPasscodeEnabled:   Boolean, // exclude
    biometryType:        String,
    isBiometricsEnabled: Boolean  // exclude
  )

  case class OperatingSystemInfo(
    os_release:             String,
    os_build:               String,
    os_version:             String,
    boot_time:              String, // exclude
    kernelVersion:          String,
    os_timeZone_identifier: String, // exclude
    os_type:                String
  )

  implicit
  val HardwareCodec: Codec[Hardware]                                = deriveCodec
  implicit val CellularInfoCodec: Codec[CellularInfo]               = deriveCodec
  implicit val LocalAuthenticationCodec: Codec[LocalAuthentication] = deriveCodec
  implicit val OperatingSystemInfoCodec: Codec[OperatingSystemInfo] = deriveCodec
}
