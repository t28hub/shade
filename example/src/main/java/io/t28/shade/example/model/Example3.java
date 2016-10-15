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
    public void intValue(int intValue) {
        this.intValue = intValue;
    }

    @Shade.Property("long_value")
    public void longValue(long longValue) {
        this.longValue = longValue;
    }

    @Shade.Property("string_value")
    public void string(String string) {
        this.string = string;
    }

    @Shade.Property("string_set_value")
    public void set(Set<String> set) {
        this.set = set;
    }

    @Shade.Property(value = "date_value", converter = DateConverter.class)
    public void date(Date date) {
        this.date = date;
    }

    @Shade.Property(value = "url_value", converter = UriConverter.class)
    public void url(Uri url) {
        this.url = url;
    }

    @Shade.Property("int_value")
    public int intValue() {
        return intValue;
    }

    @Shade.Property(value = "long_value", defValue = "1024")
    public long longValue() {
        return longValue;
    }

    @Shade.Property("string_value")
    public String string() {
        return string;
    }

    @Shade.Property("string_set_value")
    public Set<String> set() {
        return set;
    }

    @Shade.Property(value = "date_value", converter = DateConverter.class)
    public Date date() {
        return date;
    }

    @Shade.Property(value = "url_value", converter = UriConverter.class)
    public Uri url() {
        return url;
    }
}
