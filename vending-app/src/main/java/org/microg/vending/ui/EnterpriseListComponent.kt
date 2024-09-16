package org.microg.vending.ui

import android.util.Log
import android.widget.Space
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.vending.R
import com.google.android.finsky.AppInstallPolicy
import org.microg.gms.ui.TAG
import org.microg.vending.enterprise.App
import org.microg.vending.enterprise.EnterpriseApp



@Composable
fun EnterpriseListComponent(apps: List<EnterpriseApp>) {
    if (apps.isNotEmpty()) LazyColumn(Modifier.padding(16.dp)) {

        val requiredApps = apps.filter { it.policy == AppInstallPolicy.MANDATORY }
        if (requiredApps.isNotEmpty()) {
            item { InListHeading(R.string.vending_overview_enterprise_row_mandatory) }
            item { InListWarning(R.string.vending_overview_enterprise_row_mandatory_hint) }
            items(requiredApps) { AppRow(it) }
        }

        val optionalApps = apps.filter { it.policy == AppInstallPolicy.OPTIONAL }
        if (optionalApps.isNotEmpty()) {
            item { InListHeading(R.string.vending_overview_enterprise_row_offered) }
            items(optionalApps) { AppRow(it) }
        }

    } else Box(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(Modifier.align(Alignment.Center)) {
            Text(
                stringResource(R.string.vending_overview_enterprise_no_apps_available),
                textAlign = TextAlign.Center
            )
        }
    }

}

@Composable
fun InListHeading(@StringRes text: Int) {
    Text(
        stringResource(text),
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
fun InListWarning(@StringRes text: Int) {
    Column(Modifier.padding(bottom = 8.dp)) {
        Row(
            Modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.errorContainer),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                stringResource(text),
                Modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
                MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }

}

@Composable
fun AppRow(app: App) {
    Row(
        Modifier.padding(top = 8.dp, bottom = 8.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (app.iconUrl != null) {
            AsyncImage(
                model = app.iconUrl,
                modifier = Modifier.size(48.dp),
                contentDescription = null,
            )
        } else {
            Spacer(Modifier.size(48.dp))
        }
        Text(app.displayName)
    }
}

@Preview
@Composable
fun EnterpriseListComponentPreview() {
    EnterpriseListComponent(
        listOf(
            EnterpriseApp("com.android.vending", "Market", App.State.INSTALLED, null, AppInstallPolicy.MANDATORY),
            EnterpriseApp("org.mozilla.firefox", "Firefox", App.State.NOT_INSTALLED, null, AppInstallPolicy.OPTIONAL),
            EnterpriseApp("org.thoughtcrime.securesms", "Signal", App.State.NOT_INSTALLED, null, AppInstallPolicy.OPTIONAL)
        )
    )
}

@Preview
@Composable
fun EnterpriseListComponentEmptyPreview() {
    EnterpriseListComponent(emptyList())
}
