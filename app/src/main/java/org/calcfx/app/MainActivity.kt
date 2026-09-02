package org.calcfx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.calcfx.app.ui.components.*
import org.calcfx.app.ui.theme.*
import org.calcfx.app.ui.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val state by viewModel.uiState.collectAsState()

            CalcFXTheme(selectedAccent = state.accentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AmoledBlack
                ) {
                    CalculatorMainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorMainScreen(viewModel: CalculatorViewModel) {
    val state by viewModel.uiState.collectAsState()
    var isMoreMenuOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            val accent = LocalAppAccent.current
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CalcFX",
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 21.sp,
                            color = AmoledTextPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = " •",
                            fontWeight = FontWeight.Black,
                            fontSize = 21.sp,
                            color = accent
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AmoledBlack,
                    titleContentColor = AmoledTextPrimary
                )
            )
        },
        containerColor = AmoledBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // --- 1. AMOLED Minimalist Glass Display Screen ---
            DisplayScreen(
                expression = state.expression,
                cursorPosition = state.cursorPosition,
                resultPreview = state.resultPreview,
                formattedResult = state.formattedResult,
                isShiftActive = state.isShiftActive,
                isAlphaActive = state.isAlphaActive,
                isHypActive = state.isHypActive,
                angleUnit = state.angleUnit,
                mode = state.mode,
                hasMemory = state.hasMemoryValue,
                onSeekCursor = { pos -> viewModel.setCursor(pos) },
                onPasteText = { clip -> viewModel.insertText(clip) },
                onSwipeDelete = { viewModel.onKeyPress("DEL") },
                modifier = Modifier.fillMaxWidth().height(158.dp)
            )

            // --- 2. Complete Casio FX Keypad Grid with 3-Dot Menu next to MODE ---
            Keypad(
                isShiftActive = state.isShiftActive,
                isAlphaActive = state.isAlphaActive,
                onKeyPress = { key -> viewModel.onKeyPress(key) },
                onOpenMoreMenu = { isMoreMenuOpen = true },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        }
    }

    // --- 3-Dot More Menu Sheet ---
    if (isMoreMenuOpen) {
        MoreMenuBottomSheet(
            onOpenMode = { viewModel.onKeyPress("MODE"); isMoreMenuOpen = false },
            onOpenTheme = { viewModel.openThemePicker(); isMoreMenuOpen = false },
            onOpenConstants = { viewModel.onKeyPress("CONST"); isMoreMenuOpen = false },
            onOpenConverter = { viewModel.onKeyPress("CONV"); isMoreMenuOpen = false },
            onOpenHistory = { viewModel.onKeyPress("HIST"); isMoreMenuOpen = false },
            onDismiss = { isMoreMenuOpen = false }
        )
    }

    // --- Sub-Dialogs & Sheets ---
    if (state.isThemePickerOpen) {
        ThemeSelectionDialog(
            currentAccent = state.accentTheme,
            onSelectAccent = { theme -> viewModel.setAccentTheme(theme) },
            onDismiss = { viewModel.dismissDialogs() }
        )
    }

    if (state.isHistoryOpen) {
        HistoryBottomSheet(
            history = state.history,
            onItemClick = { item -> viewModel.insertHistoryItem(item) },
            onClearAll = { viewModel.clearHistory() },
            onDismiss = { viewModel.dismissDialogs() }
        )
    }

    if (state.isConstantsOpen) {
        ConstantsBottomSheet(
            onSelectConstant = { c -> viewModel.insertConstant(c) },
            onDismiss = { viewModel.dismissDialogs() }
        )
    }

    if (state.isConverterOpen) {
        UnitConverterDialog(
            onDismiss = { viewModel.dismissDialogs() }
        )
    }

    if (state.isModeDialogOpen) {
        ModeSelectionDialog(
            currentMode = state.mode,
            currentAngle = state.angleUnit,
            onSelectMode = { m -> viewModel.setCalculatorMode(m) },
            onSelectAngle = { a -> viewModel.setAngleUnit(a) },
            onDismiss = { viewModel.dismissDialogs() }
        )
    }

    if (state.isEquationOpen) {
        EquationSolverDialog(
            onDismiss = { viewModel.dismissDialogs() }
        )
    }

    if (state.isMatrixOpen) {
        MatrixSolverDialog(
            onDismiss = { viewModel.dismissDialogs() }
        )
    }

    if (state.isVectorOpen) {
        VectorSolverDialog(
            onDismiss = { viewModel.dismissDialogs() }
        )
    }

    if (state.isBaseNOpen) {
        BaseNDialog(
            onDismiss = { viewModel.dismissDialogs() }
        )
    }

    if (state.isTableOpen) {
        TableModeDialog(
            evaluator = viewModel.evaluator,
            onDismiss = { viewModel.dismissDialogs() }
        )
    }

    if (state.isStatsOpen) {
        StatisticsDialog(
            onDismiss = { viewModel.dismissDialogs() }
        )
    }
}
