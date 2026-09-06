package com.study.MementoApp.exception

class UserProfileNotFoundException(userId: String) : 
    RuntimeException("Perfil de usuario com ID '$userId' nao foi encontrado")

class MementoNotFoundException(userId: String, version: Int) : 
    RuntimeException("Historico/Versao '$version' para o usuario '$userId' nao foi encontrado")

class InvalidMementoException(message: String) : 
    RuntimeException(message)

class NoHistoryToUndoException(userId: String) : 
    RuntimeException("Nao ha historicos anteriores para desfazer (undo) no perfil '$userId'")
