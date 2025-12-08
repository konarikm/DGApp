package cz.utb.fai.dgapp.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NavigationBar(
    currentRoute: String,
    onItemSelected: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavigationItem.History,
        NavigationItem.NewGame,
        NavigationItem.Courses
    )

    NavigationBar(modifier = Modifier) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = selected,
                onClick = { onItemSelected(item) },
            )
        }
    }
}