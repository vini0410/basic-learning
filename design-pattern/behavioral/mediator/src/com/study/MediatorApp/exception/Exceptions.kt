package com.study.MediatorApp.exception

open class MediatorException(message: String) : RuntimeException(message)

class HandlerNotFoundException(commandClass: String) : 
    MediatorException("Nenhum CommandHandler foi encontrado para o comando: $commandClass")

class OrderNotFoundException(orderId: String) : 
    RuntimeException("Pedido com ID '$orderId' nao foi encontrado")

class InsufficientStockException(productId: String, requested: Int, available: Int) : 
    RuntimeException("Estoque insuficiente para o produto '$productId'. Solicitado: $requested, Disponivel: $available")

class InvalidOrderStateException(message: String) : 
    RuntimeException(message)
