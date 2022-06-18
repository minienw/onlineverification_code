package nl.rijksoverheid.minienw.travelvalidation.validationservice.services

import org.springframework.stereotype.Component
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

@Component
class CorsFilter : Filter {

    override fun doFilter(req: ServletRequest?, res: ServletResponse?, chain: FilterChain?) {
        val response = res as HttpServletResponse
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Version");
        chain!!.doFilter(req, res);
    }
}