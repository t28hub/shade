package io.t28.shade.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import io.t28.shade.annotation.Property;
import io.t28.shade.converter.DateConverter;
import io.t28.shade.converter.UriConverter;
import io.t28.shade.internal.EqualsBuilder;
import io.t28.shade.internal.HashCodeBuilder;
import io.t28.shade.internal.ToStringBuilder;
import java.util.Date;

@SuppressWarnings("all")
public class PreparedConverterPreferences {
    private final SharedPreferences preferences;

    public PreparedConverterPreferences(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences("io.t28.shade.test", 0);
    }

    @NonNull
    public PreparedConverter get() {
        return new PreparedConverterImpl(getPublished(), getWebsite());
    }

    @NonNull
    public Date getPublished() {
        return new DateConverter().toConverted(preferences.getLong("published", 0L));
    }

    @NonNull
    public Uri getWebsite() {
        return new UriConverter().toConverted(preferences.getString("website", ""));
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
    public SharedPreferences getSharedPreferences() {
        return preferences;
    }

    public static class PreparedConverterImpl extends PreparedConverter {
        private final Date published;

        private final Uri website;

        public PreparedConverterImpl(@NonNull Date published, @NonNull Uri website) {
            this.published = published;
            this.website = website;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof PreparedConverter)) {
                return false;
            }
            final PreparedConverter that = (PreparedConverter) object;
            final EqualsBuilder builder = new EqualsBuilder();
            builder.append(published, that.published());
            builder.append(website, that.website());
            return builder.build();
        }

        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(published);
            builder.append(website);
            return builder.build();
        }

        @NonNull
        @Override
        public String toString() {
            final ToStringBuilder builder = new ToStringBuilder(this);
            builder.append("published", published);
            builder.append("website", website);
            return builder.toString();
        }

        @Override
        @Property(
                key = "published",
                converter = DateConverter.class
        )
        public Date published() {
            return published;
        }

        @Override
        @Property(
                key = "website",
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
        public Editor put(@NonNull PreparedConverter preparedConverter) {
            putPublished(preparedConverter.published());
            putWebsite(preparedConverter.website());
            return this;
        }

        @NonNull
        public Editor putPublished(@NonNull Date published) {
            editor.putLong("published", new DateConverter().toSupported(published));
            return this;
        }

        @NonNull
        public Editor putWebsite(@NonNull Uri website) {
            editor.putString("website", new UriConverter().toSupported(website));
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
