package me.imadenigma.gangsplugin.exceptions

class GangNotFoundException(uuid: String): RuntimeException("Gang with the uuid $uuid is not found")