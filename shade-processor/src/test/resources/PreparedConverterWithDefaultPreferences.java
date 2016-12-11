package io.t28.shade.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.t28.shade.annotation.Property;
import io.t28.shade.converter.DateConverter;
import io.t28.shade.converter.UriConverter;
import java.util.Date;

@SuppressWarnings("all")
public class PreparedConverterWithDefaultPreferences {
    private final SharedPreferences preferences;

    public PreparedConverterWithDefaultPreferences(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences("io.t28.shade.test", 0);
    }

    @NonNull
    public PreparedConverterWithDefault get() {
        return new PreparedConverterWithDefaultImpl(getPublished(), getWebsite());
    }

    @NonNull
    public Date getPublished() {
        return new DateConverter().toConverted(preferences.getLong("published", 1512961200000L));
    }

    @NonNull
    public Uri getWebsite() {
        return new UriConverter().toConverted(preferences.getString("website", "https://github.com"));
    }

    public boolean containsPublished() {
        return preferences.contains("published");
    }

    public boolean containsWebsite() {
        return preferences.contains("website");
    }

    @NonNull
    public Editor edit() {
        return new Editor(preferences);
    }

    @NonNull
    public SharedPreferences provideSharedPreferences() {
        return preferences;
    }

    public static class PreparedConverterWithDefaultImpl extends PreparedConverterWithDefault {
        private final Date published;

        private final Uri website;

        protected PreparedConverterWithDefaultImpl(Date published, Uri website) {
            this.published = published;
            this.website = website;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof PreparedConverterWithDefault)) {
                return false;
            }
            final PreparedConverterWithDefault that = (PreparedConverterWithDefault) object;
            return Objects.equal(published, that.published()) &&
                    Objects.equal(website, that.website());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(published, website);
        }

        @NonNull
        @Override
        public String toString() {
            return MoreObjects.toStringHelper("PreparedConverterWithDefault")
                    .add("published", published)
                    .add("website", website)
                    .toString();
        }

        @Override
        @Property(
                key = "published",
                defValue = "1512961200000",
                converter = DateConverter.class
        )
        public Date published() {
            return published;
        }

        @Override
        @Property(
                key = "website",
                defValue = "https://github.com",
                converter = UriConverter.class
        )
        public Uri website() {
            return website;
        }
    }

    public static class Editor {
        private final SharedPreferences.Editor editor;

        protected Editor(@NonNull SharedPreferences preferences) {
            this.editor = preferences.edit();
        }

        @NonNull
        public Editor putPublished(@NonNull Date newValue) {
            editor.putLong("published", new DateConverter().toSupported(newValue));
            return this;
        }

        @NonNull
        public Editor putWebsite(@NonNull Uri newValue) {
            editor.putString("website", new UriConverter().toSupported(newValue));
            return this;
        }

        @NonNull
        public Editor removePublished() {
            editor.remove("published");
            return this;
        }

        @NonNull
        public Editor removeWebsite() {
            editor.remove("website");
            return this;
        }

        @NonNull
        public Editor clear() {
            editor.clear();
            return this;
        }

        public void apply() {
            editor.apply();
        }
    }
}
