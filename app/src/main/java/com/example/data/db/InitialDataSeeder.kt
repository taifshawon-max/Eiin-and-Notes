package com.example.data.db

import com.example.data.model.ClassNoteEntity
import com.example.data.model.QuestionAnswerEntity
import com.example.data.model.SchoolEntity
import com.example.data.model.UserEntitlementEntity

object InitialDataSeeder {

    fun getDefaultEntitlements(): UserEntitlementEntity {
        return UserEntitlementEntity(
            userId = "default_user",
            schoolCreditsPurchased = 100, // Initial welcome allowance
            schoolCreditsUsed = 0,
            noteCreditsPurchased = 2,     // Initial 2 free sample notes
            noteCreditsUsed = 0,
            qaCreditsPurchased = 2,       // Initial 2 Q&A credits
            qaCreditsUsed = 0,
            lastUpdated = System.currentTimeMillis()
        )
    }

    val sampleSchools = listOf(
        // Dhaka Division
        SchoolEntity(
            id = "sch_108274",
            name = "Notre Dame College, Dhaka",
            nameBn = "নটর ডেম কলেজ, ঢাকা",
            eiin = "108274",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Motijheel",
            institutionType = "College",
            board = "Dhaka",
            address = "Toyenbee Circular Road, Arambagh, Motijheel, Dhaka-1000",
            establishedYear = 1949,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_108258",
            name = "Dhaka Residential Model College",
            nameBn = "ঢাকা রেসিডেনসিয়াল মডেল কলেজ",
            eiin = "108258",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Mohammadpur",
            institutionType = "School & College",
            board = "Dhaka",
            address = "Mirpur Road, Mohammadpur, Dhaka-1207",
            establishedYear = 1960,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_108357",
            name = "Viqarunnisa Noon School and College",
            nameBn = "ভিকারুননিসা নূন স্কুল অ্যান্ড কলেজ",
            eiin = "108357",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Ramna",
            institutionType = "School & College",
            board = "Dhaka",
            address = "1/A New Bailey Road, Ramna, Dhaka-1000",
            establishedYear = 1952,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_108277",
            name = "Ideal School and College, Motijheel",
            nameBn = "আইডিয়াল স্কুল অ্যান্ড কলেজ, মতিঝিল",
            eiin = "108277",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Motijheel",
            institutionType = "School & College",
            board = "Dhaka",
            address = "Motijheel Colony, Dhaka-1000",
            establishedYear = 1965,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_108573",
            name = "Rajuk Uttara Model College",
            nameBn = "রাজউক উত্তরা মডেল কলেজ",
            eiin = "108573",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Uttara",
            institutionType = "School & College",
            board = "Dhaka",
            address = "Sector 6, Uttara Model Town, Dhaka-1230",
            establishedYear = 1994,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_108502",
            name = "Holy Cross College, Tejgaon",
            nameBn = "হলিক্রস কলেজ, তেজগাঁও",
            eiin = "108502",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Tejgaon",
            institutionType = "College",
            board = "Dhaka",
            address = "Tejgaon Industrial Area, Dhaka-1215",
            establishedYear = 1950,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_107975",
            name = "Dhaka College",
            nameBn = "ঢাকা কলেজ",
            eiin = "107975",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Dhanmondi",
            institutionType = "College",
            board = "Dhaka",
            address = "Mirpur Road, Dhanmondi, Dhaka-1205",
            establishedYear = 1841,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_107962",
            name = "Government Laboratory High School",
            nameBn = "গভর্নমেন্ট ল্যাবরেটরি হাই স্কুল",
            eiin = "107962",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Dhanmondi",
            institutionType = "High School",
            board = "Dhaka",
            address = "Nayapaltan & Dhanmondi, Dhaka-1205",
            establishedYear = 1961,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_107856",
            name = "Adamjee Cantonment College",
            nameBn = "আদমজী ক্যান্টনমেন্ট কলেজ",
            eiin = "107856",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Dhaka Cantonment",
            institutionType = "College",
            board = "Dhaka",
            address = "Dhaka Cantonment, Dhaka-1206",
            establishedYear = 1960,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_108255",
            name = "St. Joseph Higher Secondary School",
            nameBn = "সেন্ট জোসেফ উচ্চ মাধ্যমিক বিদ্যালয়",
            eiin = "108255",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Mohammadpur",
            institutionType = "Higher Secondary",
            board = "Dhaka",
            address = "97 Asad Avenue, Mohammadpur, Dhaka-1207",
            establishedYear = 1954,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_114704",
            name = "Mirzapur Cadet College",
            nameBn = "মির্জাপুর ক্যাডেট কলেজ",
            eiin = "114704",
            division = "Dhaka",
            district = "Tangail",
            upazila = "Mirzapur",
            institutionType = "Cadet College",
            board = "Dhaka",
            address = "Mirzapur, Tangail-1940",
            establishedYear = 1963,
            isVerified = true
        ),

        // Chattogram Division
        SchoolEntity(
            id = "sch_104532",
            name = "Chittagong College",
            nameBn = "চট্টগ্রাম কলেজ",
            eiin = "104532",
            division = "Chattogram",
            district = "Chattogram",
            upazila = "Kotwali",
            institutionType = "College",
            board = "Chattogram",
            address = "College Road, Chawkbazar, Chattogram-4203",
            establishedYear = 1869,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_104534",
            name = "Chittagong Collegiate School",
            nameBn = "চট্টগ্রাম কলেজিয়েট স্কুল",
            eiin = "104534",
            division = "Chattogram",
            district = "Chattogram",
            upazila = "Kotwali",
            institutionType = "School & College",
            board = "Chattogram",
            address = "Ice Factory Road, Chittagong-4000",
            establishedYear = 1836,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_105072",
            name = "Faujdarhat Cadet College",
            nameBn = "ফৌজদারহাট ক্যাডেট কলেজ",
            eiin = "105072",
            division = "Chattogram",
            district = "Chattogram",
            upazila = "Sitakunda",
            institutionType = "Cadet College",
            board = "Chattogram",
            address = "Faujdarhat, Sitakunda, Chattogram-4312",
            establishedYear = 1958,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_105826",
            name = "Comilla Zilla School",
            nameBn = "কুমিল্লা জিলা স্কুল",
            eiin = "105826",
            division = "Chattogram",
            district = "Cumilla",
            upazila = "Adarsha Sadar",
            institutionType = "High School",
            board = "Cumilla",
            address = "Kandirpar, Cumilla-3500",
            establishedYear = 1837,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_105827",
            name = "Comilla Victoria Government College",
            nameBn = "কুমিল্লা ভিক্টোরিয়া সরকারি কলেজ",
            eiin = "105827",
            division = "Chattogram",
            district = "Cumilla",
            upazila = "Adarsha Sadar",
            institutionType = "College",
            board = "Cumilla",
            address = "Victoria College Road, Cumilla-3500",
            establishedYear = 1899,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_106606",
            name = "Feni Girls Cadet College",
            nameBn = "ফেনী গার্লস ক্যাডেট কলেজ",
            eiin = "106606",
            division = "Chattogram",
            district = "Feni",
            upazila = "Feni Sadar",
            institutionType = "Cadet College",
            board = "Cumilla",
            address = "Old Airport Road, Feni-3900",
            establishedYear = 2006,
            isVerified = true
        ),

        // Rajshahi Division
        SchoolEntity(
            id = "sch_126487",
            name = "Rajshahi College",
            nameBn = "রাজশাহী কলেজ",
            eiin = "126487",
            division = "Rajshahi",
            district = "Rajshahi",
            upazila = "Boalia",
            institutionType = "College",
            board = "Rajshahi",
            address = "Rani Bazar, Boalia, Rajshahi-6000",
            establishedYear = 1873,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_126490",
            name = "Rajshahi Collegiate School",
            nameBn = "রাজশাহী কলেজিয়েট স্কুল",
            eiin = "126490",
            division = "Rajshahi",
            district = "Rajshahi",
            upazila = "Boalia",
            institutionType = "High School",
            board = "Rajshahi",
            address = "Alokar Mor, Boalia, Rajshahi-6000",
            establishedYear = 1828,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_119253",
            name = "Bogura Zilla School",
            nameBn = "বগুড়া জিলা স্কুল",
            eiin = "119253",
            division = "Rajshahi",
            district = "Bogura",
            upazila = "Bogura Sadar",
            institutionType = "High School",
            board = "Rajshahi",
            address = "Satmatha, Bogura-5800",
            establishedYear = 1853,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_125637",
            name = "Pabna Cadet College",
            nameBn = "পাবনা ক্যাডেট কলেজ",
            eiin = "125637",
            division = "Rajshahi",
            district = "Pabna",
            upazila = "Pabna Sadar",
            institutionType = "Cadet College",
            board = "Rajshahi",
            address = "Jalalpur, Pabna-6600",
            establishedYear = 1981,
            isVerified = true
        ),

        // Khulna Division
        SchoolEntity(
            id = "sch_117112",
            name = "Khulna Zilla School",
            nameBn = "খুলনা জিলা স্কুল",
            eiin = "117112",
            division = "Khulna",
            district = "Khulna",
            upazila = "Khulna Sadar",
            institutionType = "High School",
            board = "Jashore",
            address = "South Central Road, Khulna-9100",
            establishedYear = 1885,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_117110",
            name = "Government Brajalal (BL) College",
            nameBn = "সরকারি ব্রজলাল (বিএল) কলেজ",
            eiin = "117110",
            division = "Khulna",
            district = "Khulna",
            upazila = "Daulatpur",
            institutionType = "College",
            board = "Jashore",
            address = "Daulatpur, Khulna-9202",
            establishedYear = 1902,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_116560",
            name = "Jhenaidah Cadet College",
            nameBn = "ঝিনাইদহ ক্যাডেট কলেজ",
            eiin = "116560",
            division = "Khulna",
            district = "Jhenaidah",
            upazila = "Jhenaidah Sadar",
            institutionType = "Cadet College",
            board = "Jashore",
            address = "Jhenaidah-7300",
            establishedYear = 1963,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_115957",
            name = "Jessore Zilla School",
            nameBn = "যশোর জিলা স্কুল",
            eiin = "115957",
            division = "Khulna",
            district = "Jashore",
            upazila = "Kotwali",
            institutionType = "High School",
            board = "Jashore",
            address = "Mujib Sarak, Jashore-7400",
            establishedYear = 1838,
            isVerified = true
        ),

        // Barishal Division
        SchoolEntity(
            id = "sch_100742",
            name = "Barisal Zilla School",
            nameBn = "বরিশাল জিলা স্কুল",
            eiin = "100742",
            division = "Barishal",
            district = "Barishal",
            upazila = "Kotwali",
            institutionType = "High School",
            board = "Barishal",
            address = "Fazlul Haque Avenue, Barishal-8200",
            establishedYear = 1829,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_100743",
            name = "Government Brojomohun (BM) College",
            nameBn = "সরকারি ব্রজমোহন (বিএম) কলেজ",
            eiin = "100743",
            division = "Barishal",
            district = "Barishal",
            upazila = "Kotwali",
            institutionType = "College",
            board = "Barishal",
            address = "BM College Road, Barishal-8200",
            establishedYear = 1889,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_100684",
            name = "Barisal Cadet College",
            nameBn = "বরিশাল ক্যাডেট কলেজ",
            eiin = "100684",
            division = "Barishal",
            district = "Barishal",
            upazila = "Babuganj",
            institutionType = "Cadet College",
            board = "Barishal",
            address = "Babuganj, Barishal-8210",
            establishedYear = 1981,
            isVerified = true
        ),

        // Sylhet Division
        SchoolEntity(
            id = "sch_130452",
            name = "Murari Chand (MC) College, Sylhet",
            nameBn = "মুরারিচাঁদ (এমসি) কলেজ, সিলেট",
            eiin = "130452",
            division = "Sylhet",
            district = "Sylhet",
            upazila = "Sylhet Sadar",
            institutionType = "College",
            board = "Sylhet",
            address = "Tilagarh, Sylhet-3100",
            establishedYear = 1892,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_130453",
            name = "Sylhet Cadet College",
            nameBn = "সিলেট ক্যাডেট কলেজ",
            eiin = "130453",
            division = "Sylhet",
            district = "Sylhet",
            upazila = "Airport",
            institutionType = "Cadet College",
            board = "Sylhet",
            address = "Airport Road, Sylhet-3101",
            establishedYear = 1978,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_130455",
            name = "Sylhet Government Pilot High School",
            nameBn = "সিলেট সরকারি পাইলট উচ্চ বিদ্যালয়",
            eiin = "130455",
            division = "Sylhet",
            district = "Sylhet",
            upazila = "Kotwali",
            institutionType = "High School",
            board = "Sylhet",
            address = "Kalighat, Sylhet-3100",
            establishedYear = 1836,
            isVerified = true
        ),

        // Rangpur Division
        SchoolEntity(
            id = "sch_127485",
            name = "Carmichael College, Rangpur",
            nameBn = "কারমাইকেল কলেজ, রংপুর",
            eiin = "127485",
            division = "Rangpur",
            district = "Rangpur",
            upazila = "Rangpur Sadar",
            institutionType = "College",
            board = "Dinajpur",
            address = "Lalbagh, Rangpur-5400",
            establishedYear = 1916,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_127486",
            name = "Rangpur Cadet College",
            nameBn = "রংপুর ক্যাডেট কলেজ",
            eiin = "127486",
            division = "Rangpur",
            district = "Rangpur",
            upazila = "Rangpur Sadar",
            institutionType = "Cadet College",
            board = "Dinajpur",
            address = "Dhap, Rangpur-5400",
            establishedYear = 1979,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_120711",
            name = "Dinajpur Zilla School",
            nameBn = "দিনাজপুর জিলা স্কুল",
            eiin = "120711",
            division = "Rangpur",
            district = "Dinajpur",
            upazila = "Dinajpur Sadar",
            institutionType = "High School",
            board = "Dinajpur",
            address = "Balubari, Dinajpur-5200",
            establishedYear = 1854,
            isVerified = true
        ),

        // Mymensingh Division
        SchoolEntity(
            id = "sch_111837",
            name = "Mymensingh Zilla School",
            nameBn = "ময়মনসিংহ জিলা স্কুল",
            eiin = "111837",
            division = "Mymensingh",
            district = "Mymensingh",
            upazila = "Mymensingh Sadar",
            institutionType = "High School",
            board = "Mymensingh",
            address = "Brahmaputra Bank, Mymensingh-2200",
            establishedYear = 1853,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_111838",
            name = "Ananda Mohan College",
            nameBn = "আনন্দ মোহন কলেজ",
            eiin = "111838",
            division = "Mymensingh",
            district = "Mymensingh",
            upazila = "Mymensingh Sadar",
            institutionType = "College",
            board = "Mymensingh",
            address = "College Road, Mymensingh-2201",
            establishedYear = 1908,
            isVerified = true
        ),
        SchoolEntity(
            id = "sch_111839",
            name = "Mymensingh Girls Cadet College",
            nameBn = "ময়মনসিংহ গার্লস ক্যাডেট কলেজ",
            eiin = "111839",
            division = "Mymensingh",
            district = "Mymensingh",
            upazila = "Mymensingh Sadar",
            institutionType = "Cadet College",
            board = "Mymensingh",
            address = "Kashor, Mymensingh-2200",
            establishedYear = 1982,
            isVerified = true
        )
    )

    val sampleNotes = listOf(
        ClassNoteEntity(
            id = "note_phy_101",
            title = "Physics 1st Paper: Mechanics & Gravitation Mastery",
            subject = "Physics",
            courseCode = "PHY-174",
            academicLevel = "Class 11-12 (HSC)",
            author = "Prof. Anisur Rahman (Ex-Notre Dame)",
            description = "Comprehensive chapter-by-chapter handwritten lecture notes, calculus derivations for Newton's laws, Kepler's planetary motion, and solved CQ/MCQ board questions.",
            sampleExcerpt = "Preview Chapter 4: Newtonian Mechanics\n\n• Inertia and Momentum: p = mv, F = dp/dt\n• Rocket propulsion thrust formula: F_ext = v_rel * (dm/dt)\n• Conservation of Angular Momentum: L = Iω = constant\n• Centripetal Force: F_c = m*v^2 / r = m*ω^2*r\n• Banked road angle without friction: tan(θ) = v^2 / (rg)",
            fullContent = """
# Physics 1st Paper: Mechanics, Gravitation & Work-Energy
Author: Prof. Anisur Rahman | Class 11-12 (HSC) | Course Code: PHY-174

---

### CHAPTER 1: VECTORS
1. Scalar and Vector Quantities.
2. Vector Addition Laws:
   - Triangle Law, Parallelogram Law:
     R = √(P² + Q² + 2PQ cos θ)
     tan α = (Q sin θ) / (P + Q cos θ)
3. Dot Product (Scalar Product):
   - A · B = |A| |B| cos θ = A_x B_x + A_y B_y + A_z B_z
   - If A · B = 0, vectors are mutually perpendicular.
4. Cross Product (Vector Product):
   - A × B = |A| |B| sin θ η̂
   - Area of parallelogram with sides A and B = |A × B|.
5. Divergence, Curl and Gradient:
   - Grad φ = ∇φ
   - Div V = ∇ · V (Solenoidal if 0)
   - Curl V = ∇ × V (Irrotational if 0)

---

### CHAPTER 2: NEWTONIAN MECHANICS
1. Newton's Three Laws of Motion.
2. Impulse and Momentum Theorem:
   - J = ∫ F dt = Δp = m(v - u)
3. Conservation of Linear Momentum:
   - m₁u₁ + m₂u₂ = m₁v₁ + m₂v₂
4. Rocket Equation (Tsiolkovsky):
   - Thrust: F = u_rel * (dm/dt) - mg
5. Friction:
   - Static Friction: f_s ≤ μ_s R
   - Kinetic Friction: f_k = μ_k R
6. Circular Motion & Road Banking:
   - tan θ = v² / (rg)
   - With friction: tan(θ + λ) = v² / (rg) where tan λ = μ.

---

### CHAPTER 3: GRAVITATION & GRAVITY
1. Newton's Universal Law of Gravitation:
   - F = G(m₁m₂) / r² (G = 6.673 × 10⁻¹¹ N m²/kg²)
2. Acceleration due to gravity at height h:
   - g_h = g(1 - 2h/R) [for h << R] or g_h = g(R / (R + h))²
3. Acceleration at depth d:
   - g_d = g(1 - d/R)
4. Escape Velocity:
   - v_e = √(2gR) = √(2GM / R) ≈ 11.2 km/s on Earth.
5. Kepler's Three Laws:
   - Law of Orbits (Elliptical)
   - Law of Areas (dA/dt = L / 2m = constant)
   - Law of Periods (T² ∝ r³)

---

### BOARD EXAM HIGH-YIELD CQ PROBLEMS & SOLUTIONS
Problem 1: A car of mass 1200 kg navigates a circular bend of radius 80m banked at 15°. Calculate the safest speed to avoid skidding when friction is negligible.
Solution:
tan θ = v² / (rg) => v = √(rg tan θ) = √(80 * 9.8 * tan 15°) = √(784 * 0.2679) = 14.49 m/s (52.17 km/h).
            """.trimIndent(),
            totalPages = 28,
            priceCredits = 1,
            downloadsCount = 389
        ),
        ClassNoteEntity(
            id = "note_che_102",
            title = "Chemistry 2nd Paper: Complete Organic Chemistry Reactions",
            subject = "Chemistry",
            courseCode = "CHE-177",
            academicLevel = "Class 11-12 (HSC)",
            author = "Dr. M. Rezaul Karim (BUET / DU Visiting)",
            description = "All named reactions, IUPAC nomenclature, electrophilic and nucleophilic substitution mechanisms, isomerism charts, and conversion cheat sheets for board exams.",
            sampleExcerpt = "Sample Excerpt:\n\n• Electrophilic Aromatic Substitution: Nitration, Halogenation, Friedel-Crafts Alkylation & Acylation\n• Markovnikov's Rule and Anti-Markovnikov (Peroxide Effect)\n• Grignard Reagent synthesis: R-Mg-X + HCHO -> 1° alcohol; RCHO -> 2° alcohol; R2CO -> 3° alcohol\n• Lucas Reagent test for alcohol distinction (Anhydrous ZnCl2 + conc. HCl)",
            fullContent = """
# Chemistry 2nd Paper: Organic Chemistry Master Review
Author: Dr. M. Rezaul Karim | HSC / Medical & Engineering Admission

---

### SECTION 1: ISOMERISM
1. Structural Isomerism:
   - Chain, Positional, Functional Group, Metamerism, Tautomerism (Keto-Enol).
2. Stereoisomerism:
   - Geometric (Cis-Trans): Restricted rotation around C=C or ring.
   - Optical: Asymmetric chiral carbon, non-superimposable mirror images (enantiomers).
   - Diastereomers and Meso compounds.

---

### SECTION 2: HYDROCARBONS & MECHANISMS
1. Alkanes:
   - Wurtz Reaction: 2 R-X + 2 Na (dry ether) -> R-R + 2 NaX
   - Decarboxylation: R-COONa + NaOH (CaO) -> R-H + Na2CO3
2. Alkenes & Alkynes:
   - Markovnikov's Rule: Electrophilic addition of HX to asymmetric alkene.
   - Ozonolysis: O3 followed by Zn/H2O -> Aldehydes and Ketones identification.
3. Benzene & Aromatic Compounds:
   - Huckel's Rule: (4n + 2) pi electrons, cyclic, planar, conjugated.
   - Ortho-Para Directing: -OH, -NH2, -CH3, -OCH3 (Activating groups).
   - Meta Directing: -NO2, -COOH, -CHO, -CN, -SO3H (Deactivating groups).

---

### SECTION 3: ORGANIC SYNTHESIS ROADMAPS
• Conversion 1: Ethyne to Benzoic Acid
  1. 3 C2H2 --(Red hot Fe tube, 400°C)--> Benzene
  2. Benzene + CH3Cl --(Anhydrous AlCl3)--> Toluene
  3. Toluene --(KMnO4, H2SO4, heat)--> Benzoic Acid

• Conversion 2: Nitrobenzene to Phenol
  1. Nitrobenzene --(Sn + HCl)--> Aniline
  2. Aniline --(NaNO2 + HCl, 0-5°C)--> Benzene Diazonium Chloride
  3. Diazonium salt --(H2O, warm)--> Phenol + N2 + HCl
            """.trimIndent(),
            totalPages = 34,
            priceCredits = 1,
            downloadsCount = 512
        ),
        ClassNoteEntity(
            id = "note_math_101",
            title = "Higher Math 1st Paper: Calculus & Coordinate Geometry",
            subject = "Higher Math",
            courseCode = "MTH-178",
            academicLevel = "Class 11-12 (HSC)",
            author = "Engr. Tanvir Ahmed (Ex-NDC, BUET)",
            description = "Step-by-step calculus mastery: Limits, standard derivatives, chain rule, maximum/minimum value problems, indefinite & definite integration techniques with shortcuts.",
            sampleExcerpt = "Key Formulas:\n\n• Limit x->0 of (sin x)/x = 1, (e^x - 1)/x = 1, (ln(1+x))/x = 1\n• d/dx [u/v] = (v du/dx - u dv/dx) / v^2\n• Integration by Parts: ∫ u v dx = u ∫ v dx - ∫ [ (du/dx) ∫ v dx ] dx (LIATE rule)\n• Tangent & Normal Equations to Curve y = f(x) at (x1, y1)",
            fullContent = """
# Higher Mathematics: Calculus & Analytical Geometry
Author: Engr. Tanvir Ahmed | Board Exam & Admission Guide

---

### PART 1: LIMITS & CONTINUITY
1. Fundamental Theorems of Limits:
   - lim (x->a) [x^n - a^n] / [x - a] = n a^(n-1)
   - lim (x->0) [sin x / x] = 1 (where x is in radians)
   - lim (x->0) [(1 + x)^(1/x)] = e
2. L'Hopital's Rule for 0/0 and ∞/∞ forms:
   - Differentiate numerator and denominator independently with respect to x.

---

### PART 2: DIFFERENTIATION
1. Standard Derivatives:
   - d/dx [sin⁻¹ x] = 1 / √(1 - x²)
   - d/dx [tan⁻¹ x] = 1 / (1 + x²)
   - d/dx [a^x] = a^x ln a
2. Successive Differentiation & Leibniz Theorem:
   - (uv)_n = u_n v + nC1 u_(n-1) v_1 + nC2 u_(n-2) v_2 + ... + u v_n
3. Maxima and Minima:
   - Necessary Condition: dy/dx = 0
   - Sufficient Condition: d²y/dx² < 0 for Maximum; d²y/dx² > 0 for Minimum.

---

### PART 3: INTEGRATION
1. Standard Forms:
   - ∫ dx / (x² + a²) = (1/a) tan⁻¹(x/a) + C
   - ∫ dx / √(a² - x²) = sin⁻¹(x/a) + C
   - ∫ dx / √(x² ± a²) = ln |x + √(x² ± a²)| + C
2. Definite Integral as Area under Curve:
   - Area = ∫[a to b] y dx
   - Area between two curves y1 and y2: Area = ∫[a to b] (y1 - y2) dx
            """.trimIndent(),
            totalPages = 42,
            priceCredits = 1,
            downloadsCount = 420
        ),
        ClassNoteEntity(
            id = "note_bio_101",
            title = "Biology 1st & 2nd Paper: Genetics, Cell Division & Physiology",
            subject = "Biology",
            courseCode = "BIO-175",
            academicLevel = "Class 11-12 (HSC)",
            author = "Dr. Nusrat Jahan (DMC)",
            description = "High-scoring diagrams, Mendel's laws of inheritance, ABO blood groups, DNA replication, human circulatory system, nephron structure, and hormone functions.",
            sampleExcerpt = "Key Points:\n\n• Mendel's 1st Law (Monohybrid Cross): 3:1 phenotypic, 1:2:1 genotypic ratio\n• Incomplete Dominance ratio: 1:2:1; Co-dominance: 1:2:1\n• Mendel's 2nd Law (Dihybrid Cross): 9:3:3:1 ratio\n• Epistasis modifications: Dominant epistasis (12:3:1), Recessive epistasis (9:3:4), Duplicate recessive (9:7)",
            fullContent = """
# Biology: High Yield Board & Medical Admission Notes
Author: Dr. Nusrat Jahan | MBBS (Dhaka Medical College)

---

### CHAPTER 1: CELL STRUCTURE & GENETICS
1. Organelles:
   - Mitochondria (Powerhouse of the cell, 70S ribosome, circular DNA).
   - Ribosome: 70S in Prokaryotes (50S + 30S), 80S in Eukaryotes (60S + 40S).
   - Chloroplast: Thylakoid, Stroma, Site of photosynthesis light and dark reactions.
2. DNA Replication:
   - Semiconservative model proved by Meselson and Stahl.
   - Key enzymes: Helicase, DNA Polymerase III, Primase, Ligase.
3. Protein Synthesis:
   - Transcription (DNA -> mRNA via RNA Polymerase II).
   - Translation (mRNA -> Polypeptide at ribosome with tRNA anticodon).

---

### CHAPTER 2: HUMAN PHYSIOLOGY - CIRCULATION
1. Heart Anatomy:
   - 4 chambers: Right/Left Atrium, Right/Left Ventricle.
   - Valves: Tricuspid, Bicuspid (Mitral), Aortic Semilunar, Pulmonary Semilunar.
2. Cardiac Cycle:
   - Atrial Systole (0.1s), Atrial Diastole (0.7s)
   - Ventricular Systole (0.3s), Ventricular Diastole (0.5s)
   - Total duration: 0.8s per beat (approx 72-75 bpm).
3. Pacemaker:
   - Sinoatrial (SA) Node: Primary pacemaker (generates 70-80 impulses/min).
   - Atrioventricular (AV) Node, Bundle of His, Purkinje Fibers.
            """.trimIndent(),
            totalPages = 31,
            priceCredits = 1,
            downloadsCount = 295
        ),
        ClassNoteEntity(
            id = "note_ict_201",
            title = "HSC ICT: HTML Web Design, C Programming & SQL Database",
            subject = "ICT",
            courseCode = "ICT-275",
            academicLevel = "Class 11-12 (HSC)",
            author = "Md. Arifur Rahman (BSc in CSE)",
            description = "Complete guide to HSC ICT Chapter 3 (Number systems, Logic gates), Chapter 4 (HTML tables, links, images), and Chapter 5 (C language loops, arrays, functions, flowcharts).",
            sampleExcerpt = "Chapter Highlights:\n\n• Binary, Octal, Decimal & Hexadecimal conversions with 2's complement\n• Universal Gates: NAND and NOR gate implementations\n• HTML Table structure: <table>, <tr>, <th>, <td>, colspan, rowspan\n• C Programming: for, while, do-while loops, if-else, functions, and prime number algorithms",
            fullContent = """
# HSC ICT: Comprehensive Complete Board Revision Guide
Author: Md. Arifur Rahman | CSE, DU

---

### CHAPTER 3: NUMBER SYSTEMS & DIGITAL LOGIC
1. Conversion Techniques:
   - Decimal to Binary/Octal/Hex: Successive division by base.
   - Fractional Decimal: Successive multiplication by base.
2. 2's Complement Arithmetic:
   - 1's complement = Invert bits (0->1, 1->0)
   - 2's complement = 1's complement + 1
   - Subtraction performed as addition of 2's complement.
3. Boolean Algebra & Logic Gates:
   - De Morgan's Laws: (A + B)' = A' · B' ; (A · B)' = A' + B'
   - Universal Gates: NAND and NOR can emulate AND, OR, NOT.
   - Half Adder: Sum = A ⊕ B, Carry = A · B
   - Full Adder: Sum = A ⊕ B ⊕ C_in, Carry = AB + BC_in + CA_in

---

### CHAPTER 4: HTML & WEB DESIGN
1. Basic Structure:
   <!DOCTYPE html>
   <html>
     <head><title>Page Title</title></head>
     <body>
       <h1>Heading 1</h1>
       <p>Paragraph text</p>
       <a href="https://example.com">Hyperlink</a>
       <img src="photo.jpg" alt="Description" width="300" />
     </body>
   </html>
2. Tables with Colspan & Rowspan:
   <table border="1">
     <tr><th colspan="2">Merged Header</th></tr>
     <tr><td rowspan="2">Rowspan</td><td>Data 1</td></tr>
     <tr><td>Data 2</td></tr>
   </table>

---

### CHAPTER 5: C PROGRAMMING FUNDAMENTALS
1. Prime Number Algorithm in C:
   #include <stdio.h>
   int main() {
       int n, i, isPrime = 1;
       printf("Enter a number: ");
       scanf("%d", &n);
       if (n <= 1) isPrime = 0;
       for (i = 2; i * i <= n; i++) {
           if (n % i == 0) { isPrime = 0; break; }
       }
       if (isPrime) printf("%d is prime\n", n);
       else printf("%d is not prime\n", n);
       return 0;
   }
            """.trimIndent(),
            totalPages = 24,
            priceCredits = 1,
            downloadsCount = 630
        ),
        ClassNoteEntity(
            id = "note_ssc_phy",
            title = "SSC Physics: Full Syllabus Formulas, Concepts & CQ Drills",
            subject = "Physics",
            courseCode = "SSC-PHY",
            academicLevel = "Class 9-10 (SSC)",
            author = "Masterclass Academy Bangladesh",
            description = "Tailored for Class 9 and 10 SSC candidates. Covers Motion, Force, Work Power Energy, Pressure, Heat, Light Reflection/Refraction, Electricity, and Modern Physics.",
            sampleExcerpt = "Essential Formulas:\n\n• v = u + at, s = ((u+v)/2)*t, s = ut + (1/2)at^2, v^2 = u^2 + 2as\n• Force: F = ma; Momentum: p = mv\n• Work: W = Fs cos θ; Kinetic Energy: T = (1/2)mv^2; Potential Energy: V = mgh\n• Power: P = W/t; Efficiency: η = (Output Work / Input Energy) * 100%",
            fullContent = """
# SSC Physics: Core Revision Sheet
Subject: Physics | Class 9-10 / SSC Exam Preparation

---

### CHAPTER 2: MOTION
- Distance vs Displacement: Distance is scalar, displacement is vector.
- Speed vs Velocity: v = s / t.
- Acceleration: a = (v - u) / t.
- Equations of uniform acceleration:
  1. v = u + at
  2. s = ((u + v) / 2) * t
  3. s = ut + 0.5 * a * t²
  4. v² = u² + 2as

---

### CHAPTER 3: FORCE & MOMENTUM
- Newton's Laws:
  - First Law: Inertia.
  - Second Law: Rate of change of momentum is proportional to applied force (F = ma).
  - Third Law: To every action there is an equal and opposite reaction.
- Conservation of Momentum in collision:
  m₁u₁ + m₂u₂ = m₁v₁ + m₂v₂

---

### CHAPTER 5: PRESSURE AND STATES OF MATTER
- Pressure P = F / A (Unit: Pascal, N/m²).
- Liquid Pressure: P = h ρ g.
- Archimedes' Principle: Buoyant force = Weight of displaced liquid (F_b = V ρ g).
- Pascal's Law: F₂ / A₂ = F₁ / A₁.
            """.trimIndent(),
            totalPages = 20,
            priceCredits = 1,
            downloadsCount = 740
        )
    )

    val sampleQuestions = listOf(
        QuestionAnswerEntity(
            id = "qa_eiin_xi_admission",
            questionTitle = "How do I use the 6-digit School/College EIIN during XI Class Online Admission?",
            questionDetails = "When applying on xiclassadmission.gov.bd, where do I enter the college EIIN and how many preference choices can I submit?",
            category = "EIIN & Admission",
            academicLevel = "College Admission",
            askedBy = "Sadia Islam (SSC Candidate, Dhaka)",
            answerPreview = "During XI Class Centralized Online Admission, you can select between 5 and 10 colleges by entering their 6-digit EIIN or selecting by Board -> District -> Thana...",
            fullAnswer = """
### Official XI Class Admission EIIN Guide (Bangladesh Inter-Education Board)

1. **Where to Use the 6-Digit EIIN**:
   - On the official portal (`xiclassadmission.gov.bd`), after verifying your SSC Roll, Registration Number, Passing Year, and Board, you will enter the **College Selection Step**.
   - Selecting the correct **6-digit EIIN** guarantees you do not accidentally pick a similarly named college in another district.

2. **Minimum & Maximum Choices**:
   - You must select **at least 5 colleges** and up to **10 colleges** in order of preference (1st choice = highest priority).
   - For colleges with multiple shifts (Morning / Day) or versions (Bangla / English), each shift/version counts as a separate preference entry under the same EIIN.

3. **Key Verification Checklist**:
   - Always verify the **Board**, **District**, **Upazila**, and **Minimum GPA Requirement** of the EIIN before submitting.
   - Note: Notre Dame College (EIIN: `108274`), Holy Cross College (EIIN: `108502`), and St. Joseph Higher Secondary School (EIIN: `108255`) conduct their own separate admission process under High Court approval.
            """.trimIndent(),
            answeredBy = "Prof. M. Mahbubur Rahman (Admission Advisor)",
            isUserAsked = false,
            createdAt = "2026-03-22",
            helpfulCount = 186
        ),
        QuestionAnswerEntity(
            id = "qa_phy_banking_angle",
            questionTitle = "How to solve HSC Physics road banking CQ when both banking angle and friction coefficient are given?",
            questionDetails = "In board exams, sometimes they give both the banking angle θ and coefficient of static friction μ_s. What is the exact formula for maximum safe velocity?",
            category = "Physics",
            academicLevel = "Class 11-12 (HSC)",
            askedBy = "Tanvir Hossain (HSC Science, Rajshahi)",
            answerPreview = "When road friction μ_s is present alongside banking angle θ, you must use the combined angle of repose formula: v_max = √[ r g (tan θ + μ_s) / (1 - μ_s tan θ) ]...",
            fullAnswer = """
### Step-by-Step Solution: Banked Road with Friction (HSC Physics 1st Paper)

#### 1. Case A: Smooth Road (Friction Negligible, μ_s = 0)
• tan θ = v² / (r g)  ⇒  v = √(r g tan θ)

#### 2. Case B: Rough Banked Road (Both θ and μ_s Given)
When a vehicle tends to skid outward at maximum speed, static friction f_s = μ_s N acts down the incline:
• Horizontal component: N sin θ + f_s cos θ = (m v_max²) / r
• Vertical component: N cos θ - f_s sin θ = m g

Dividing the two equations and substituting f_s = μ_s N:
• v_max = √[ r g (tan θ + μ_s) / (1 - μ_s tan θ) ]

Or using the angle of friction λ where tan λ = μ_s:
• v_max = √[ r g tan(θ + λ) ]

#### 3. Minimum Speed to Prevent Sliding Downward:
• v_min = √[ r g (tan θ - μ_s) / (1 + μ_s tan θ) ]
            """.trimIndent(),
            answeredBy = "Prof. Anisur Rahman (Physics Dept.)",
            isUserAsked = false,
            createdAt = "2026-03-19",
            helpfulCount = 142
        ),
        QuestionAnswerEntity(
            id = "qa_che_aldol_cannizzaro",
            questionTitle = "How to distinguish between Aldol Condensation and Cannizzaro Reaction in HSC Chemistry 2nd Paper?",
            questionDetails = "Board CQ questions always give two carbonyl compounds A and B and ask which one undergoes Aldol vs Cannizzaro. How do I identify them quickly?",
            category = "Chemistry",
            academicLevel = "Class 11-12 (HSC)",
            askedBy = "Nusrat Faria (Chattogram Board)",
            answerPreview = "The key difference is the presence of an alpha-hydrogen (α-H). Aldehydes/ketones WITH α-H undergo Aldol condensation in dilute alkali, whereas aldehydes WITHOUT α-H undergo Cannizzaro...",
            fullAnswer = """
### Distinguishing Aldol Condensation vs. Cannizzaro Reaction

#### 1. The Golden Rule: Check the α-Hydrogen (α-H)
The carbon atom directly attached to the carbonyl group (-CHO or >C=O) is the α-carbon, and hydrogens attached to it are α-hydrogens.

---

#### 2. Aldol Condensation
- Condition: Must have at least one α-H + Dilute NaOH / KOH (10%) at room/moderate temperature.
- Examples:
  • Ethanal (CH₃CHO) → has 3 α-H → forms 3-hydroxybutanal (Aldol).
  • Propanone (CH₃COCH₃) → has 6 α-H.

---

#### 3. Cannizzaro Reaction (Disproportionation / Self Oxidation-Reduction)
- Condition: Must have NO α-H + Concentrated NaOH / KOH (50%).
- Examples:
  • Methanal / Formaldehyde (HCHO) → no α-carbon.
  • Benzaldehyde (C₆H₅CHO) → α-carbon has no H.
  • Trimethylacetaldehyde ((CH₃)₃C-CHO).
- Products: One molecule is oxidized to a carboxylic acid salt (HCOONa / C₆H₅COONa) and another molecule is reduced to an alcohol (CH₃OH / C₆H₅CH₂OH).
            """.trimIndent(),
            answeredBy = "Dr. M. Rezaul Karim (Chemistry Specialist)",
            isUserAsked = false,
            createdAt = "2026-03-17",
            helpfulCount = 119
        ),
        QuestionAnswerEntity(
            id = "qa_ict_2s_complement",
            questionTitle = "Why is 2's complement used in digital computers and how do I subtract a larger number from a smaller number?",
            questionDetails = "In HSC ICT Chapter 3, when subtracting 25 from 17 using 8-bit 2's complement, how do I interpret the negative result?",
            category = "ICT",
            academicLevel = "Class 11-12 (HSC)",
            askedBy = "Rafiul Alam (Cumilla Board)",
            answerPreview = "Using 2's complement allows the CPU adder circuit to perform both addition and subtraction. When the sign bit (8th bit) is 1, take the 2's complement of the result to verify the magnitude...",
            fullAnswer = """
### 8-Bit 2's Complement Subtraction: 17 - 25 = 17 + (-25)

#### Step 1: Convert Both Numbers to 8-Bit Binary
• +17 = 00010001₂
• +25 = 00011001₂

#### Step 2: Find -25 using 2's Complement
1. 1's Complement of 25 (invert all bits):
   11100110₂
2. Add 1 to get 2's Complement:
   11100110₂ + 1 = 11100111₂ (This represents -25)

#### Step 3: Add +17 and (-25)
   00010001  (+17)
+  11100111  (-25)
-----------
   11111000  (Result in 2's complement form)

#### Step 4: Interpret the Result
- The Sign Bit (MSB, 8th bit) is 1, which means the result is negative.
- To verify its magnitude, take the 2's complement of 11111000:
  • 1's complement: 00000111
  • Add 1: 00001000 = 8 (decimal)
- Therefore, the answer is -8 (11111000 in 8-bit register).
            """.trimIndent(),
            answeredBy = "Md. Arifur Rahman (ICT Lecturer)",
            isUserAsked = false,
            createdAt = "2026-03-14",
            helpfulCount = 98
        )
    )
}
