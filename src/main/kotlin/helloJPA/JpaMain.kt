package helloJPA

import helloJPA.entity.Member
import helloJPA.entity.Team
import org.omg.CORBA.Object
import javax.persistence.*
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root
import kotlin.reflect.typeOf

class JpaMain {

}

fun main() {
    //criteriaRun()
    //queryDslRun()
    //nativeQuery()
    jpqlRun()
}

fun jpqlRun() {
    val emf: EntityManagerFactory = Persistence.createEntityManagerFactory("hello")
    val em: EntityManager = emf.createEntityManager()
em.flushMode
    val tx: EntityTransaction = em.transaction
    tx.begin()

    try {
        em.flushMode.
        //val query: TypedQuery<Member> =
/*            em.createQuery("select m From Member m where m.name = :name", Member::class.java)
        query.setParameter("name", "재진")
        val result: Member = query.singleResult
*/
/*
        em.createQuery("select m From Member m where m.name = ?1", Member::class.java)
        query.setParameter(1, "재진")
        val result: Member = query.singleResult
*/


/*
        val query: Query = em.createQuery("select m From Member m")
        val result = query.resultList
*/
/*
        val result: List<Member> = em.createQuery("select m from Member m", Member::class.java).resultList
        val findMember: Member = result[0]
        findMember.age = 35*/

        // val result: List<Team> = em.createQuery("select t from Member m join m.team t", Team::class.java).resultList
/*
        val result: List<Member> = em.createQuery("select m from Member m ", Member::class.java)
            .setFirstResult(1)
            .setMaxResults(10)
            .resultList
*/
/*
        val query:String = "select mm from (select m.age from Member m) as mm"
        val result: List<String>
                = em.createQuery(query).resultList as List<String>
*/
        val result: List<Member>
                = em.createQuery("select m from Member m, Team t where m.name = t.name").resultList. as List<Member>

        val result: List<Member>
        = em.createQuery("select m from Member m, Team t where m.name = t.name").resultList as List<Member>


        tx.commit()
    } catch (e: Exception) {
        tx.rollback()
        e.printStackTrace()
    } finally {
        em.close()
    }

    emf.close()
}

fun criteriaRun() {

    val emf: EntityManagerFactory = Persistence.createEntityManagerFactory("hello")
    val em: EntityManager = emf.createEntityManager()

    val tx: EntityTransaction = em.transaction
    tx.begin()

    try {

        val cb: CriteriaBuilder = em.criteriaBuilder
        val query: CriteriaQuery<Member> = cb.createQuery(Member::class.java)

        val m: Root<Member> = query.from(Member::class.java)

        val cq: CriteriaQuery<Member> = query.select(m).where(cb.equal(m.get<String>("name1"), "재진"))
        val result: List<Member> = em.createQuery(cq).resultList


//
//        val result: List<Member> = em.createQuery(
//            "select m From Member m where m.name like '%재진%'", Member::class.java
//        ).resultList

        tx.commit()
    } catch (e: Exception) {
        tx.rollback()
        e.printStackTrace()
    } finally {
        em.close()
    }

    emf.close()
}

/*
fun queryDslRun() {
    val queryFactory: JPA
    val m: QMember = QMember.member
    val result: List<Member> = queryFactory.select(m).from(m).where(m.name.like("재진")).fetch()
}*/

fun nativeQuery() {

    val emf: EntityManagerFactory = Persistence.createEntityManagerFactory("hello")
    val em: EntityManager = emf.createEntityManager()

    val tx: EntityTransaction = em.transaction
    tx.begin()

    try {
        val sql = "SELECT id, name, age from MEMBER"
        val result: List<Member> = em.createNativeQuery(sql, Member::class.java).resultList as List<Member>
        tx.commit()
    } catch (e: Exception) {
        tx.rollback()
        e.printStackTrace()
    } finally {
        em.close()
    }

    emf.close()

}
