package info.plateaukao.einkbro.view.dialog.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import icu.xmc.edgettslib.entity.VoiceItem
import icu.xmc.edgettslib.entity.dummyVoiceItem
import info.plateaukao.einkbro.R
import info.plateaukao.einkbro.service.TtsManager
import info.plateaukao.einkbro.unit.IntentUnit
import info.plateaukao.einkbro.view.compose.MyTheme
import info.plateaukao.einkbro.view.compose.SelectableText
import info.plateaukao.einkbro.view.dialog.TtsLanguageDialog
import info.plateaukao.einkbro.view.dialog.TtsTypeDialog
import info.plateaukao.einkbro.viewmodel.TtsType
import org.koin.core.component.inject
import java.util.Locale

class TtsSettingDialogFragment : ComposeDialogFragment() {
    private val ttsManager: TtsManager by inject()

    override fun setupComposeView() {
        composeView.setContent {
            MyTheme {
                val ttsType = remember { mutableStateOf(config.ttsType) }
                val ettsVoice = remember { mutableStateOf(config.ettsVoice) }
                MainTtsSettingDialog(
                    selectedType = ttsType.value,
                    selectedLocale = config.ttsLocale,
                    selectedEttsVoice = ettsVoice.value,
                    selectedSpeedValue = config.ttsSpeedValue,
                    onSpeedValueClick = { config.ttsSpeedValue = it; dismiss() },
                    recentVoices = config.recentUsedTtsVoices,
                    onVoiceSelected = { config.ettsVoice = it; dismiss() },
                    okAction = { dismiss() },
                    gotoSettingAction = { IntentUnit.gotoSystemTtsSettings(requireActivity()) },
                    showLocaleDialog = {
                        TtsLanguageDialog(requireContext()).show(ttsManager.getAvailableLanguages())
                    },
                    showTtsTypeDialog = {
                        TtsTypeDialog(requireContext()).show {
                            ttsType.value = it
                        }
                    },
                    showEttsVoiceDialog = {
                        ETtsVoiceDialogFragment { ettsVoice.value = it }
                            .show(parentFragmentManager, "etts_voice")
                    }
                )
            }
        }
    }
}

private val speedRateValueList = listOf(
    50,
    75,
    100,
    125,
)

private val speedRateValueList2 = listOf(
    150,
    175,
    200
)

@Composable
private fun MainTtsSettingDialog(
    selectedType: TtsType,
    selectedLocale: Locale,
    selectedEttsVoice: VoiceItem,
    recentVoices: List<VoiceItem>,
    selectedSpeedValue: Int,
    onSpeedValueClick: (Int) -> Unit,
    gotoSettingAction: () -> Unit,
    okAction: () -> Unit,
    onVoiceSelected: (VoiceItem) -> Unit,
    showLocaleDialog: () -> Unit,
    showTtsTypeDialog: () -> Unit,
    showEttsVoiceDialog: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .width(IntrinsicSize.Max)
    ) {
        Text(
            stringResource(id = R.string.setting_tts_type),
            modifier = Modifier.padding(vertical = 6.dp),
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
        )
        SelectableText(
            modifier = Modifier.padding(horizontal = 1.dp, vertical = 3.dp),
            selected = true, text = selectedType.name
        ) {
            showTtsTypeDialog()
        }
        if (selectedType == TtsType.ETTS) {
            Text(
                stringResource(id = R.string.setting_tts_voice),
                modifier = Modifier.padding(vertical = 6.dp),
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
            )
            SelectableText(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 3.dp),
                selected = true,
                text = Locale(selectedEttsVoice.getLanguageCode()).displayName
                        + " " + selectedEttsVoice.getVoiceRole()
            ) {
                showEttsVoiceDialog()
            }
            recentVoices.filterNot { it.Name == selectedEttsVoice.Name }.forEach { voice ->
                SelectableText(
                    modifier = Modifier.padding(horizontal = 1.dp, vertical = 3.dp),
                    selected = false,
                    text = Locale(voice.getLanguageCode()).displayName
                            + " " + voice.getVoiceRole()
                ) {
                    onVoiceSelected(voice)
                }
            }
            SelectableText(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 3.dp),
                selected = true,
                text = LocalContext.current.getString(R.string.other_voices)
            ) {
                showEttsVoiceDialog()
            }
        }
        if (selectedType == TtsType.SYSTEM) {
            Text(
                stringResource(id = R.string.setting_tts_locale),
                modifier = Modifier.padding(vertical = 6.dp),
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
            )
            SelectableText(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 3.dp),
                selected = true, text = selectedLocale.displayName
            ) {
                showLocaleDialog()
            }
        }
        Text(
            stringResource(id = R.string.read_speed),
            modifier = Modifier.padding(vertical = 6.dp),
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
        )
        Row {
            speedRateValueList.map { speedRate ->
                val isSelect = selectedSpeedValue == speedRate
                SelectableText(
                    modifier = Modifier
                        .padding(horizontal = 1.dp, vertical = 3.dp),
                    selected = isSelect,
                    text = "$speedRate%",
                ) {
                    onSpeedValueClick(speedRate)
                }
            }
        }
        Row {
            speedRateValueList2.map { speedRate ->
                val isSelect = selectedSpeedValue == speedRate
                SelectableText(
                    modifier = Modifier
                        .padding(horizontal = 1.dp, vertical = 3.dp),
                    selected = isSelect,
                    text = "$speedRate%",
                ) {
                    onSpeedValueClick(speedRate)
                }
            }
        }
        TtsDialogButtonBar(
            showSystemSetting = selectedType == TtsType.SYSTEM,
            gotoSettingAction = gotoSettingAction,
            okAction = okAction,
        )
    }
}

@Composable
fun TtsDialogButtonBar(
    showSystemSetting: Boolean,
    gotoSettingAction: () -> Unit,
    okAction: () -> Unit,
) {
    Column {
        HorizontalSeparator()
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (showSystemSetting) {
                TextButton(
                    modifier = Modifier.wrapContentWidth(),
                    onClick = gotoSettingAction
                ) {
                    Text(
                        stringResource(id = R.string.system_settings),
                        color = MaterialTheme.colors.onBackground
                    )
                }
                VerticalSeparator()
            }
            TextButton(
                modifier = Modifier.wrapContentWidth(),
                onClick = okAction
            ) {
                Text(
                    stringResource(id = R.string.close),
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}

@Preview(widthDp = 600, showBackground = true)
@Composable
fun PreviewMainTtsDialog() {
    MyTheme {
        MainTtsSettingDialog(
            selectedType = TtsType.SYSTEM,
            selectedLocale = Locale.US,
            selectedSpeedValue = 100,
            recentVoices = listOf(dummyVoiceItem),
            onVoiceSelected = {},
            onSpeedValueClick = {},
            okAction = {},
            gotoSettingAction = {},
            showLocaleDialog = {},
            showTtsTypeDialog = {},
            showEttsVoiceDialog = {},
            selectedEttsVoice = dummyVoiceItem
        )
    }
}