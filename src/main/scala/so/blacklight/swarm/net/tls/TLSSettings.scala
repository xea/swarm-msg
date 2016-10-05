package so.blacklight.swarm.net.tls

import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.{KeyManager, TrustManager, X509TrustManager}

/**
	*
	*/
case class TLSSettings(keyStore: KeyStore, trustStore: KeyStore);

class PermissiveTrustManager extends X509TrustManager {

	override def getAcceptedIssuers: Array[X509Certificate] = {
		Array()
	}

	override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String) = {

	}

	override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String) = {

	}
}
