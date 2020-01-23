package by.integrated.notification.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column

data class Notification(
    @Id
    val id: Long?,
    @Column("user_id")
    val user: Long
)