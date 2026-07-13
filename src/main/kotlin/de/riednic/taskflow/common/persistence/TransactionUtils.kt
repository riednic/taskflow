package de.riednic.taskflow.common.persistence

import org.springframework.transaction.interceptor.TransactionAspectSupport

fun markRollbackOnly() {
    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
}
