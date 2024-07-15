package com.example.adoptify_core.ui.adopt.process.cat

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.adoptify_core.R
import com.example.adoptify_core.databinding.FragmentAgreementBinding
import com.example.adoptify_core.ui.auth.login.LoginViewModel
import com.example.adoptify_core.ui.main.MainViewModel
import com.example.core.data.Resource
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.O)
class AgreementFragment : Fragment() {

    private var _agreementFragment: FragmentAgreementBinding? = null

    private val agreementFragment get() = _agreementFragment!!

    private val mainViewModel: MainViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()

    private var token = ""
    private var userId by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _agreementFragment = FragmentAgreementBinding.inflate(inflater, container, false)
        return _agreementFragment?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        getToken()
        getUserId()
        setupListener()
        setupView()

    }

    private fun initData() {
        loginViewModel.getSession()
        mainViewModel.getUserId()
    }

    private fun getToken() {
        loginViewModel.token.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    token = it.data
                    Log.d("AgreementFragment", "check: $token")
                }
                is Resource.Error -> {}
            }
        }
    }

    private fun getUserId() {
        mainViewModel.userId.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    userId = it.data
                    Log.d("AgreementFragment", "check: $userId")
                }
                is Resource.Error -> {
                }
            }
        }
    }

    private fun setupListener() {
        agreementFragment.apply {
            switchButton.setOnCheckedChangeListener { _, isChecked ->
                btnNext.isEnabled = isChecked
                btnNext.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    if (isChecked) R.color.primaryColor else R.color.btn_disabled
                )
                btnNext.setTextColor(if (isChecked) resources.getColor(R.color.white) else resources.getColor(R.color.black))
            }

            val isButtonEnable = switchButton.isChecked
            btnNext.isEnabled = isButtonEnable
            btnNext.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (isButtonEnable) R.color.primaryColor else R.color.btn_disabled
            )
            btnNext.setTextColor(if (isButtonEnable) resources.getColor(R.color.white) else resources.getColor(R.color.black))


            val viewPager = activity?.findViewById<ViewPager2>(R.id.processViewPager)
            btnBack.setOnClickListener { activity?.finish() }
            btnNext.setOnClickListener { viewPager?.currentItem = 1 }
        }
    }


    private fun setupView() {
        val spannableString = SpannableStringBuilder()

        val extraBoldTypeface = resources.getFont(R.font.plus_jakarta_sans_extra)
        val semiBoldTypeface = resources.getFont(R.font.plus_jakarta_sans_semibold)
        val normalTypeface = resources.getFont(R.font.plus_jakarta_sans_regular)

        val extraBoldSpan = CustomTypefaceSpan(extraBoldTypeface)
        val semiBoldSpan = CustomTypefaceSpan(semiBoldTypeface)
        val normalSpan =  CustomTypefaceSpan(normalTypeface)

        // Header
        val header1 = SpannableString("SYARAT & KETENTUAN UNTUK PARA ADOPTER\n\n")
        header1.setSpan(extraBoldSpan, 0, header1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        header1.setSpan(AbsoluteSizeSpan(14, true), 0, header1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(header1)

        // Point 1
        val section1 = SpannableString("1. PELIHARA HEWAN ADALAH KOMITMEN SEUMUR HIDUP\n")
        section1.setSpan(semiBoldSpan, 0, section1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        section1.setSpan(AbsoluteSizeSpan(14, true), 0, section1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(section1)

        val paragraph1 = SpannableString("Peliharan hewan adalah sebuah komitmen seumur hidup yang melibatkan tanggung jawab dan perhatian yang besar terhadap makhluk hidup yang kamu adopsi sebagai hewan peliharaan. Ini adalah kontrak emosional yang kita buat dengan hewan tersebut, di mana kita berjanji untuk memberikan perawatan dan cinta yang tak terbatas selama mereka hidup.\n\n")
        paragraph1.setSpan(normalSpan, 0, paragraph1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        paragraph1.setSpan(AbsoluteSizeSpan(14, true), 0, paragraph1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(paragraph1)

        val paragraph2 = SpannableString("Sebelum Kamu benar-benar yakin untuk mengadopsi hewan, Kamu juga harus mempertimbangkan kemungkinan adanya perubahan dalam gaya hidup Kamu yang bisa mempengaruhi bahkan mengancam kesejahteraan hewan yang akan diadopsi seperti:\n")
        paragraph2.setSpan(normalSpan, 0, paragraph2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        paragraph2.setSpan(AbsoluteSizeSpan(14, true), 0, paragraph2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(paragraph2)

        val bulletList1 = SpannableString("• Menikah\n• Hamil/mempunyai anak\n• Pindah Domisili/ Tempat Kerja\n• Adanya anak kecil atau anggota keluarga lain yang sudah tua atau memiliki kondisi khusus dalam satu rumah tinggal dengan kamu (misalnya: masalah kesehatannya seperti alergi pada bulu hewan)\n• Bencana alam\n• dan berbagai alasan/faktor lain.\n\n")
        bulletList1.setSpan(normalSpan, 0, bulletList1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        bulletList1.setSpan(AbsoluteSizeSpan(14, true), 0, bulletList1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(bulletList1)

        // Point 2
        val section2 = SpannableString("2. HARUS SIAP FISIK, MENTAL & FINANSIAL\n")
        section2.setSpan(semiBoldSpan, 0, section2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        section2.setSpan(AbsoluteSizeSpan(14, true), 0, section2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(section2)

        val paragraph3 = SpannableString("Sebagai calon adopter, Kamu harus memahami bahwa pelihara kucing lebih kompleks. Makanan, air, tempat tinggal yang aman dan nyaman, perawatan medis yang tepat, dan stimulasi mental adalah beberapa aspek penting yang harus dipertimbangkan untuk memenuhi kebutuhan dasar kucing.\n\n")
        paragraph3.setSpan(normalSpan, 0, paragraph3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        paragraph3.setSpan(AbsoluteSizeSpan(14, true), 0, paragraph3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(paragraph3)

        val paragraph4 = SpannableString("Beberapa pertanyaan yang mungkin Kamu bisa renungkan sebelum pelihara kucing, seperti:\n")
        paragraph4.setSpan(normalSpan, 0, paragraph4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        paragraph4.setSpan(AbsoluteSizeSpan(14, true), 0, paragraph4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(paragraph4)

        val bulletList2 = SpannableString("• Apakah Kamu memiliki waktu & tenaga untuk melakukan perawatan terhadap kucing kamu seperti: mengajak bermain, merawat ketika sakit, membersihkan pup/pip nya di litter box mereka dsb?\n• Apakah Kamu siap memahami karakter kucing & menghadapi kucing ketika melakukan kesalahan tidak di sengaja seperti memecahkan pot/ gelas, buang air sembarangan dsb?\n• Apakah Kamu memiliki anggaran & bersedia untuk mengeluarkan dana di luar kebutuhan pokok (makan, minum & pasir kucing) untuk kebutuhan lainnya seperti: vitamin, vaksin, kucing berobat ke vet ketika sakit dsb?\n\n")
        bulletList2.setSpan(normalSpan, 0, bulletList2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        bulletList2.setSpan(AbsoluteSizeSpan(14, true), 0, bulletList2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(bulletList2)

        // Point 3
        val section3 = SpannableString("3. PRO STERIL, PRO VAKSIN & PRO VET\n")
        section3.setSpan(semiBoldSpan, 0, section3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        section3.setSpan(AbsoluteSizeSpan(14, true), 0, section3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(section3)

        val paragraph5 = SpannableString("Perlu diketahui, saat ini Kucing yang akan di adopsi dari kami, sudah di steril dan vaksin semua. Namun, kucing tetap butuh pengulangan (booster) vaksin tahunan agar kesehatan mereka selalu terjaga dengan baik. Pengulangan vaksin tahunan bisa di lakukan di dokter hewan\n\n")
        paragraph5.setSpan(normalSpan, 0, paragraph5.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        paragraph5.setSpan(AbsoluteSizeSpan(14, true), 0, paragraph5.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(paragraph5)

        val paragraph6 = SpannableString("Jika kucing sakit, kamu akan bersedia membawanya ke dokter hewan untuk di periksa lebih lanjut dan mendapatkan perawatan yang tepat sesuai dengan kebutuhan kucing berdasarkan diagnosa & obat yang diberikan dokter hewan.\n\n")
        paragraph6.setSpan(normalSpan, 0, paragraph6.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        paragraph6.setSpan(AbsoluteSizeSpan(14, true), 0, paragraph6.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(paragraph6)

        // Point 4
        val section4 = SpannableString("4. KUCING DI PELIHARA FULL INDOOR\n")
        section4.setSpan(semiBoldSpan, 0, section4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        section4.setSpan(AbsoluteSizeSpan(14, true), 0, section4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(section4)

        val paragraph7 = SpannableString("Untuk perawatan yang maksimal terhadap kucing, diharapkan Kamu bersedia memelihara kucing didalam rumah (full indoor) agar menjaga keselamatan dan menghindari kucing dari bahaya yang ada di luar rumah, seperti:\n")
        paragraph7.setSpan(normalSpan, 0, paragraph7.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        paragraph7.setSpan(AbsoluteSizeSpan(14, true), 0, paragraph7.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(paragraph7)

        val bulletList3 = SpannableString("• Kecelakaan karena kendaraan\n• Kucing Tersesat\n• Kucing lebih mudah terpapar virus, kuman, parasit (kutu & cacing) jika berada di luar rumah / berinteraksi dengan hewan liar lainnya (termasuk sesama kucing yang liar)\n\n")
        bulletList3.setSpan(normalSpan, 0, bulletList3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        bulletList3.setSpan(AbsoluteSizeSpan(14, true), 0, bulletList3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(bulletList3)

        // Point 5
        val section5 = SpannableString("5. MEMBERIKAN PAKAN BERNUTRISI & BERMUTU\n")
        section5.setSpan(semiBoldSpan, 0, section5.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        section5.setSpan(AbsoluteSizeSpan(14, true), 0, section5.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(section5)

        val paragraph8 = SpannableString("Bersedia memberikan pakan yang bermutu sebagai investasi jangka panjang kesehatan kucing.\n\n")
        paragraph8.setSpan(normalSpan, 0, paragraph8.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        paragraph8.setSpan(AbsoluteSizeSpan(14, true), 0, paragraph8.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(paragraph8)

        // Point 6
        val section6 = SpannableString("6. BERSEDIA MEMBERIKAN KABAR KUCING YANG DI ADOPSI\n")
        section6.setSpan(semiBoldSpan, 0, section6.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        section6.setSpan(AbsoluteSizeSpan(14, true), 0, section6.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(section6)

        val paragraph9 = SpannableString("Bersedia memberi kabar tentang perkembangan kucing dalam bentuk foto atau video, sedikitnya 3 kali dalam sebulan selama 3 (tiga) bulan pertama sejak resmi diadopsi, dan jika sewaktu-waktu diminta.\n\n")
        paragraph9.setSpan(normalSpan, 0, paragraph9.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        paragraph9.setSpan(AbsoluteSizeSpan(14, true), 0, paragraph9.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(paragraph9)

        // Point 7
        val section7 = SpannableString("7. TIDAK MEMINDAH TANGANKAN (MENGHIBAHKAN) KUCING KE PIHAK LAIN\n")
        section7.setSpan(semiBoldSpan, 0, section7.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        section7.setSpan(AbsoluteSizeSpan(14, true), 0, section7.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(section7)

        val paragraph10 = SpannableString("Bersedia untuk tidak menghibahkan kucing yang telah di adopsi dari kami ke orang lain dengan alasan apapun.\n\n")
        paragraph10.setSpan(normalSpan, 0, paragraph10.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        paragraph10.setSpan(AbsoluteSizeSpan(14, true), 0, paragraph10.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(paragraph10)

        // Point 8
        val section8 = SpannableString("8. MENANDATANGANI PERJANJIAN ADOPSI\n")
        section8.setSpan(semiBoldSpan, 0, section8.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        section8.setSpan(AbsoluteSizeSpan(14, true), 0, section8.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(section8)

        val paragraph11 = SpannableString("Jika kamu berminat untuk adopsi kucing dari kami, maka kamu akan diminta untuk menandatangani perjanjian adopsi di atas meterai, karena hal tersebut adalah sebagai bentuk keseriusan dan komitmen adopter dalam merawat anabul tersayang.")
        paragraph11.setSpan(normalSpan, 0, paragraph11.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        paragraph11.setSpan(AbsoluteSizeSpan(14, true), 0, paragraph11.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.append(paragraph11)

        agreementFragment.cardAgreement.txtTerms.text = spannableString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _agreementFragment = null
    }

}


class CustomTypefaceSpan(private val newType: Typeface) : TypefaceSpan("") {

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
        val oldStyle: Int
        val old = paint.typeface
        oldStyle = old?.style ?: 0

        val fake = oldStyle and tf.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }
        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }

        paint.typeface = tf
    }
}
