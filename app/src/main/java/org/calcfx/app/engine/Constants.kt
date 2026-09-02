package org.calcfx.app.engine

/**
 * 40 Standard Scientific Constants (CODATA values matching Casio fx-991 constants table).
 */
data class ScientificConstant(
    val code: Int,
    val symbol: String,
    val name: String,
    val value: Double,
    val unit: String,
    val category: String
)

object Constants {
    val ALL = listOf(
        ScientificConstant(1, "m_p", "Proton mass", 1.67262192369e-27, "kg", "Universal"),
        ScientificConstant(2, "m_n", "Neutron mass", 1.67492749804e-27, "kg", "Universal"),
        ScientificConstant(3, "m_e", "Electron mass", 9.1093837015e-31, "kg", "Universal"),
        ScientificConstant(4, "m_μ", "Muon mass", 1.883531627e-28, "kg", "Universal"),
        ScientificConstant(5, "a_0", "Bohr radius", 5.29177210903e-11, "m", "Atomic"),
        ScientificConstant(6, "h", "Planck constant", 6.62607015e-34, "J s", "Universal"),
        ScientificConstant(7, "μ_N", "Nuclear magneton", 5.0507837461e-27, "J/T", "Universal"),
        ScientificConstant(8, "μ_B", "Bohr magneton", 9.2740100783e-24, "J/T", "Universal"),
        ScientificConstant(9, "ℏ", "Reduced Planck const", 1.054571817e-34, "J s", "Universal"),
        ScientificConstant(10, "α", "Fine-structure const", 7.2973525693e-3, "", "Universal"),
        ScientificConstant(11, "r_e", "Classical electron radius", 2.8179403262e-15, "m", "Atomic"),
        ScientificConstant(12, "λ_C", "Compton wavelength", 2.42631023867e-12, "m", "Atomic"),
        ScientificConstant(13, "γ_p", "Proton gyromag ratio", 2.6752218744e8, "s⁻¹ T⁻¹", "Atomic"),
        ScientificConstant(14, "λ_Cp", "Proton Compton wave", 1.32140985396e-15, "m", "Atomic"),
        ScientificConstant(15, "λ_Cn", "Neutron Compton wave", 1.31959090581e-15, "m", "Atomic"),
        ScientificConstant(16, "R_∞", "Rydberg constant", 10973731.568160, "m⁻¹", "Atomic"),
        ScientificConstant(17, "u", "Atomic mass constant", 1.66053906660e-27, "kg", "Atomic"),
        ScientificConstant(18, "μ_p", "Proton magnetic moment", 1.41060679736e-26, "J/T", "Universal"),
        ScientificConstant(19, "μ_e", "Electron mag moment", -9.2847647043e-24, "J/T", "Universal"),
        ScientificConstant(20, "μ_n", "Neutron mag moment", -9.6623651e-27, "J/T", "Universal"),
        ScientificConstant(21, "μ_μ", "Muon mag moment", -4.49044830e-26, "J/T", "Universal"),
        ScientificConstant(22, "F", "Faraday constant", 96485.33212, "C/mol", "Universal"),
        ScientificConstant(23, "e", "Elementary charge", 1.602176634e-19, "C", "Universal"),
        ScientificConstant(24, "N_A", "Avogadro constant", 6.02214076e23, "mol⁻¹", "Universal"),
        ScientificConstant(25, "k", "Boltzmann constant", 1.380649e-23, "J/K", "Universal"),
        ScientificConstant(26, "V_m", "Molar vol of ideal gas", 0.02271095464, "m³/mol", "Universal"),
        ScientificConstant(27, "R", "Molar gas constant", 8.314462618, "J/(mol K)", "Universal"),
        ScientificConstant(28, "c_0", "Speed of light in vac", 299792458.0, "m/s", "Universal"),
        ScientificConstant(29, "c_1", "First radiation const", 3.741771852e-16, "W m²", "Universal"),
        ScientificConstant(30, "c_2", "Second radiation const", 1.438776877e-2, "m K", "Universal"),
        ScientificConstant(31, "σ", "Stefan-Boltzmann const", 5.670374419e-8, "W/(m² K⁴)", "Universal"),
        ScientificConstant(32, "ε_0", "Electric constant (vac)", 8.8541878128e-12, "F/m", "Universal"),
        ScientificConstant(33, "μ_0", "Magnetic constant (vac)", 1.25663706212e-6, "N/A²", "Universal"),
        ScientificConstant(34, "Φ_0", "Magnetic flux quantum", 2.067833848e-15, "Wb", "Universal"),
        ScientificConstant(35, "g", "Std accel of gravity", 9.80665, "m/s²", "Universal"),
        ScientificConstant(36, "G_0", "Conductance quantum", 7.748091729e-5, "S", "Universal"),
        ScientificConstant(37, "Z_0", "Characteristic imped", 376.730313668, "Ω", "Universal"),
        ScientificConstant(38, "t", "Celsius temperature", 273.15, "K", "Universal"),
        ScientificConstant(39, "G", "Newtonian grav const", 6.67430e-11, "m³/(kg s²)", "Universal"),
        ScientificConstant(40, "atm", "Standard atmosphere", 101325.0, "Pa", "Universal")
    )
}
