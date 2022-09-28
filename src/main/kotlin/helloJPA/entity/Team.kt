package helloJPA.entity

import javax.persistence.*

@Entity
@Table(name = "TEAM")
data class Team(
    @Id @GeneratedValue
    val id: Long,
    val name: String,

    @OneToMany(mappedBy = "team")
    val members: List<Member> = ArrayList()
) {

}