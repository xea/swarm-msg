package so.blacklight.swarm.net.tls

import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
	*
	*/
case class TLSSettings(keyStore: KeyStore, trustStore: KeyStore)

/**
	* Trust manager implementation that accepts any TLS connection regardless of the validity
	* of their certificates. This class is inherently insecure and never should be used in
	* production code.
	*/
class PermissiveTrustManager extends X509TrustManager {

	override def getAcceptedIssuers: Array[X509Certificate] = Array()

	override def checkClientTrusted(x509Certificates: Array[X509Certificate], authType: String) = Unit

	override def checkServerTrusted(x509Certificates: Array[X509Certificate], authType: String) = Unit
}
