package helloJPA.entity

import javax.persistence.*

@Entity
@Table(name = "MEMBER")
data class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    var name: String,
    var age: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    var team: Team
)