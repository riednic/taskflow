package de.riednic.taskflow.common

import org.springframework.transaction.interceptor.TransactionAspectSupport

/**
 * Marks the current transaction as rollback-only. Needed wherever a failure is represented as a
 * sealed result value instead of a thrown exception, since Spring's transaction proxy never sees
 * such a failure and would otherwise commit any writes made earlier in the same transaction.
 */
fun markRollbackOnly() {
    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
}
