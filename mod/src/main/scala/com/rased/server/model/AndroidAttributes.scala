package com.rased.server.model

object AndroidAttributes {
  case class OsData(
    gsfId:      String,
    mediaDrmId: String,
    androidId:  String
  )

  case class DisplayInfo(
    scaledDensity:            Double,
    density:                  Double,
    heightPixels:             Double,
    transitionAnimationScale: Boolean,
    widthPixels:              Double,
    windowAnimationScale:     Boolean
  )

  case class LocaleInfo(
    time12Or24:      String,
    dateFormat:      String,
    regionCountry:   String,
    timezone:        String,
    defaultLanguage: String
  )

  case class SoftwareInfo(
    displayInfo: DisplayInfo,
    localeInfo:  LocaleInfo
  )

  case class HardwareInfo(
    manufacturerName:          String,
    modelName:                 String,
    totalRam:                  Int,
    totalInternalStorageSpace: String,
    procCpuInfo:               String,
    procCpuInfoV2:             String,
    glesVersion:               String,
    abiType:                   String,
    coresCount:                String
  )
}
