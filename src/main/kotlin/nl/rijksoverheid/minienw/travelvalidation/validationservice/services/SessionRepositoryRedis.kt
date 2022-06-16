package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import com.google.gson.Gson
import org.springframework.stereotype.Service
import redis.clients.jedis.Jedis
import java.util.*

@Service
class SessionRepositoryRedis(val appSettings: IApplicationSettings) : ISessionRepository {
    override fun save(sessionInfo: SessionInfo) {
        val session = createSession() //TODO settings
        try {
            //TODO not threadsafe/tx safe
//            val subjectId = nextSubjectValue()
//            while (session.get(subjectId) != null)
//                subjectId = nextSubjectValue()
//            sessionInfo.response.subjectId = subjectId
            val value = Gson().toJson(sessionInfo)
            session.set(sessionInfo.response.subjectId, value)
            //session.expire(sessionInfo.response.subjectId, 3600); //TODO parse expiry time from sessionInfo
        } finally {
            session.client.close()
        }
    }

    private fun createSession(): Jedis {
        val session = Jedis(appSettings.redisHost, 6379)
        //session.auth("noneShallPass") //TODO settings
        return session
    }

    override fun find(subject: String): SessionInfo? {
        val session = createSession() //TODO settings
        try {
            val value = session.get(subject)
            return if (value == null) null else Gson().fromJson(value, SessionInfo::class.java)
        } finally {
            session.client.close()
        }
    }

    override fun remove(subject: String) {
        val session = createSession() //TODO settings
        try {
        } finally {
            session.client.close()
        }
    }

    //Simple (naive?) toHex?
    private fun nextSubjectValue() = UUID.randomUUID().toString().replace("-", "").uppercase(Locale.getDefault())
}

