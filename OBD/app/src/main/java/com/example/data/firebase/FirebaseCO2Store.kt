package ro.upt.ac.chiuitter.data.firebase

import com.example.data.firebase.CO2Node
import com.example.data.firebase.CO2Repository
import com.example.domain.CO2Val
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseCO2Store : CO2Repository {

    private val database = FirebaseDatabase.getInstance().reference.child("values")

    override suspend fun getAll(): List<CO2Val> = suspendCoroutine { continuation ->
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                database.removeEventListener(this)
                continuation.resumeWithException(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val values = mutableListOf<CO2Node>()
                if(p0.children.count()>0) {
                    val children = p0.children


                    //TODO ("Iterate through the children and get the node value")

                    for (child in children) {
                        values.add(child.getValue(CO2Node::class.java)!!)
                    }
                }

                database.removeEventListener(this)

                continuation.resume(values.map { co2Node -> co2Node.toDomainModel() })
            }

        })
    }

    override suspend fun addVal(co2Val: CO2Val): Unit = suspendCoroutine { continuation ->
        //TODO ("Insert the object into database - don't forget to use the right model")
        val valNode = CO2Node(co2Val.timestamp,co2Val.latitude,co2Val.longitude,co2Val.gramsPerSec)

        database.child(valNode.timestamp.toString()).setValue(valNode)

        //TODO ("Make sure the continuation is called")
        continuation.resume(Unit)
    }

    override suspend fun removeVal(co2Val: CO2Val) : Unit = suspendCoroutine { continuation ->
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                database.removeEventListener(this)
                continuation.resumeWithException(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val children = p0.children.first().children


                //TODO ("Iterate through the children and find the matching node, then perform removal.")
                var found : Long = 0
                for (child in children) {
                        if (child.getValue(CO2Node::class.java)!!.timestamp == co2Val.timestamp) {
                            found = co2Val.timestamp
                        }
                }

                database.child("chiuits").child(found.toString()).setValue(null)

                database.removeEventListener(this)

                //TODO ("Make sure the continuation is called")
                continuation.resume(Unit)
            }

        })
    }

    fun CO2Val.toFirebaseModel(): CO2Node {
        return CO2Node(timestamp, latitude,longitude,gramsPerSec)
    }

    fun CO2Node.toDomainModel(): CO2Val {
        return CO2Val(timestamp, latitude,longitude,gramsPerSec)
    }

}