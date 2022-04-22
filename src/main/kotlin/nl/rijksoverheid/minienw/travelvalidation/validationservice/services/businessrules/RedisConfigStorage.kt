package nl.rijksoverheid.minienw.travelvalidation.validationservice.services.businessrules

import nl.rijksoverheid.minienw.travelvalidation.validationservice.services.IApplicationSettings
import org.springframework.stereotype.Service
import redis.clients.jedis.Jedis

@Service
class RedisConfigStorage(private val appSettings: IApplicationSettings)
{
    fun save(name: String, value: String) {
        val session = createSession() //TODO settings
        try {
            session.set(name, value);
            session.expire(name, 120) //TODO setting
        } finally {
            session.client.close()
        }
    }

    private fun createSession(): Jedis {
        val session = Jedis(appSettings.redisHost, 6379)
        //session.auth("noneShallPass") //TODO settings
        return session
    }

    fun find(name: String): String? {
        val session = createSession() //TODO settings
        return try {
            session.get(name)
        } catch(ex: Exception) {
            null
        } finally {
            session.client.close()
        }
    }
}