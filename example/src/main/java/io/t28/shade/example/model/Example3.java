package io.t28.shade.example.model;

import android.net.Uri;

import java.util.Date;
import java.util.Set;

import io.t28.shade.annotations.Shade;
import io.t28.shade.example.converters.DateConverter;
import io.t28.shade.example.converters.UriConverter;

@Shade.Preference("io.t28.shade.example")
public class Example3 {
    private int intValue;

    private long longValue;

    private String string;

    private Set<String> set;

    private Date date;

    private Uri url;

    @Shade.Property("int_value")
    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    @Shade.Property(value = "long_value", defValue = "1024")
    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    @Shade.Property("string_value")
    public void setString(String string) {
        this.string = string;
    }

    @Shade.Property("string_set_value")
    public void setSet(Set<String> set) {
        this.set = set;
    }

    @Shade.Property(value = "date_value", converter = DateConverter.class)
    public void setDate(Date date) {
        this.date = date;
    }

    @Shade.Property(value = "url_value", converter = UriConverter.class)
    public void setUrl(Uri url) {
        this.url = url;
    }

    @Shade.Property("int_value")
    public int getIntValue() {
        return intValue;
    }

    @Shade.Property(value = "long_value", defValue = "1024")
    public long getLongValue() {
        return longValue;
    }

    @Shade.Property("string_value")
    public String getString() {
        return string;
    }

    @Shade.Property("string_set_value")
    public Set<String> getSet() {
        return set;
    }

    public Date getDate() {
        return date;
    }

    public Uri getUrl() {
        return url;
    }
}
