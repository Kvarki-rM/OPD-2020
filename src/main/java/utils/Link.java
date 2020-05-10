package utils;

import okhttp3.HttpUrl;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class Link {
    private static final String DEFAULT_PROTOCOL = "http";
    private static final Pattern schemePattern = Pattern.compile("^[a-z][a-z0-9]*://");
    private HttpUrl httpUrl;
    private String strUrl;

    // wrong url changes to ""
    // removes trailing slash
    public Link(String urlStr) {
        if (urlStr.equals("")) {
            httpUrl = null;
        } else {
            try {
                httpUrl = HttpUrl.get(fix(urlStr));
            } catch (Exception e) {
                httpUrl = null;
            }
        }
    }

    private static String fix(String url) {
        if (url.isEmpty()) return url;
        var urlFixed = fixProtocol(url);
        return fixTrailingSlash(urlFixed);
    }

    private static String fixProtocol(String url) {
        if (schemePattern.matcher(url).find()) {
            return url;
        } else {
            return DEFAULT_PROTOCOL + "://" + url;
        }
    }

    private static String fixTrailingSlash(String url) {
        if (url.charAt(url.length() - 1) == '/') {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static Link createEmptyLink() {
        return new Link("");
    }

    public Link fixWWW() {
        var fixed = getWithoutProtocol();
        if (fixed.startsWith("www.")) {
            fixed = fixed.substring(4);
        }
        return new Link(fixed);
    }

    public Set<Parameter> getParams() {
        var res = new HashSet<Parameter>();
        var query = getQuery();
        if (query != null) {
            var querySplitted = query.split("&");
            for (String parameter : querySplitted) {
                var paramSplitted = parameter.split("=");
                if (paramSplitted.length == 2) {
                    var name = paramSplitted[0];
                    var value = paramSplitted[1];
                    res.add(new Parameter(name, value));
                }
            }
        }
        return res;
    }

    public Set<String> getSubdomains() {
        var res = new HashSet<String>();
        var host = getHost();
        var hostSplitted = host.split("\\.");
        var levelsNumber = hostSplitted.length;
        // ignore top-level and second-level domain
        for (int i = 0; i < levelsNumber - 2; i++) {
            // ignore www
            var subdomain = hostSplitted[i];
            if (!subdomain.equals("www")) {
                res.add(subdomain);
            }
        }
        return res;
    }

    public String getWithoutQueryAndFragment() {
        var str = getScheme() + "://" + getHost();
        var port = getPort();
        if (port != -1) {
            str = str + ":" + port;
        }
        str += getPath();
        return str;
    }

    public String getWithoutProtocol() {
        var url = new StringBuilder();
        var userInfo = getUserInfo();
        if (userInfo != null) {
            url.append(userInfo).append("@");
        }
        url.append(getHost());
        var port = getPort();
        if (port != -1) {
            url.append(":").append(port);
        }
        var path = getPath();
        if (!path.equals("")) {
            url.append(path);
        }
        var query = getQuery();
        if (query != null) {
            url.append("?").append(query);
        }
        var fragment = getFragment();
        if (fragment != null) {
            url.append("#").append(fragment);
        }
        return url.toString();
    }

    public String getAbsoluteURL() {
        if (httpUrl == null) return null;
        return toString();
    }

    public String getScheme() {
        if (httpUrl == null) return null;
        return httpUrl.scheme();
    }

    public String getUserInfo() {
        if (httpUrl == null) return null;
        var username = httpUrl.username();
        var password = httpUrl.password();
        var userInfo = new StringBuilder();
        if (!username.equals("")) {
            userInfo.append(username);
            if (!password.equals("")) {
                userInfo.append(password);
            }
            return userInfo.toString();
        } else {
            return null;
        }
    }

    public String getHost() {
        if (httpUrl == null) return null;
        return httpUrl.host();
    }

    public int getPort() {
        if (httpUrl == null) return -1;
        int port = httpUrl.port();
        if (port == 80 || port == 443) return -1;
        return port;
    }

    public String getPath() {
        if (httpUrl == null) return "";
        var pathSeg = httpUrl.pathSegments();
        if (pathSeg.size() == 1 && pathSeg.get(0).equals("")) {
            return "";
        } else {
            return "/" + String.join("/", pathSeg);
        }
    }

    public String getQuery() {
        if (httpUrl == null) return null;
        return httpUrl.query();
    }

    public String getFragment() {
        if (httpUrl == null) return null;
        return httpUrl.fragment();
    }

    @Override
    public String toString() {
        if (httpUrl == null) return "";
        if (strUrl == null) {
            strUrl = getScheme() + "://" + getWithoutProtocol();
        }
        return strUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(httpUrl, link.httpUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpUrl);
    }
}
