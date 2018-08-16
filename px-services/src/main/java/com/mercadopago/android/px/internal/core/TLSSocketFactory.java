package com.mercadopago.android.px.internal.core;

import android.support.annotation.Keep;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

/**
 * Socket factory enabling TLS
 * <p>
 * Source: http://blog.dev-area.net/2015/08/13/android-4-1-enable-tls-1-1-and-tls-1-2/
 */
@Keep
public class TLSSocketFactory extends SSLSocketFactory {

    @SuppressWarnings("PMD.ImmutableField")
    private final SSLSocketFactory delegate;

    public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        delegate = context.getSocketFactory();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(final Socket s, final String host, final int port, final boolean autoClose)
        throws IOException {
        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localHost, final int localPort)
        throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(final InetAddress host, final int port) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddress,
        final int localPort) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket enableTLSOnSocket(final Socket socket) {
        if (socket instanceof SSLSocket) {
            ((SSLSocket) socket).setEnabledProtocols(new String[] { "TLSv1.1", "TLSv1.2" });
        }
        return socket;
    }

    public static OkHttpClient enforceTls(final OkHttpClient client) {
        try {
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
            }
            final X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            final OkHttpClient.Builder builder = client.newBuilder()
                .sslSocketFactory(new TLSSocketFactory(), trustManager);

            final ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1)
                .build();

            final List<ConnectionSpec> specs = new ArrayList<>();
            specs.add(cs);
            specs.add(ConnectionSpec.COMPATIBLE_TLS);
            specs.add(ConnectionSpec.CLEARTEXT);

            return builder.connectionSpecs(specs).build();
        } catch (final NoSuchAlgorithmException | KeyStoreException | KeyManagementException exception) {
            throw new RuntimeException(exception);
        }
    }
}