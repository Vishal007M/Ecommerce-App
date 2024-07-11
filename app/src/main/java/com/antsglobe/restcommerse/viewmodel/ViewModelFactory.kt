package com.antsglobe.restcommerse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.antsglobe.restcommerse.network.ApiService

class ViewModelFactory(private val apiService: ApiService) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> SignUpViewModel(apiService) as T
            modelClass.isAssignableFrom(TokenViewModel::class.java) -> TokenViewModel(apiService) as T
            modelClass.isAssignableFrom(GetAdminTokenViewModel::class.java) -> GetAdminTokenViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(apiService) as T
            modelClass.isAssignableFrom(OtpViewModel::class.java) -> OtpViewModel(apiService) as T
            modelClass.isAssignableFrom(ResetPassViewModel::class.java) -> ResetPassViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> NotificationViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(apiService) as T
            modelClass.isAssignableFrom(AllMostPopularViewModel::class.java) -> AllMostPopularViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(TopMostPopularViewModel::class.java) -> TopMostPopularViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(HomeCategoryViewModel::class.java) -> HomeCategoryViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(AllCategoryViewModel::class.java) -> AllCategoryViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(ProductListViewModel::class.java) -> ProductListViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(ProductViewModel::class.java) -> ProductViewModel(apiService) as T
            modelClass.isAssignableFrom(UpdateProfileViewModel::class.java) -> UpdateProfileViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(WishlistViewmodel::class.java) -> WishlistViewmodel(
                apiService
            ) as T

            modelClass.isAssignableFrom(ReviewViewModel::class.java) -> ReviewViewModel(apiService) as T
            modelClass.isAssignableFrom(AddReviewViewModel::class.java) -> AddReviewViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(CartListViewModel::class.java) -> CartListViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(AddressListViewModel::class.java) -> AddressListViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(AddressViewModel::class.java) -> AddressViewModel(apiService) as T
            modelClass.isAssignableFrom(PaymentViewModel::class.java) -> PaymentViewModel(apiService) as T
            modelClass.isAssignableFrom(VersionViewModel::class.java) -> VersionViewModel(apiService) as T

            modelClass.isAssignableFrom(CouponViewModel::class.java) -> CouponViewModel(apiService) as T
            modelClass.isAssignableFrom(AllProductListViewModel::class.java) -> AllProductListViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(MyOrderViewModel::class.java) -> MyOrderViewModel(apiService) as T
            modelClass.isAssignableFrom(ToBeReviewedViewModel::class.java) -> ToBeReviewedViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(ReviewedViewModel::class.java) -> ReviewedViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(SearchListViewModel::class.java) -> SearchListViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(GetAllPinCodeViewModel::class.java) -> GetAllPinCodeViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(DonePaymentViewModel::class.java) -> DonePaymentViewModel(
                apiService
            ) as T

            modelClass.isAssignableFrom(TaxAmountViewModel::class.java) -> TaxAmountViewModel(
                apiService
            ) as T

            else -> throw java.lang.IllegalArgumentException("ViewModel not found")
        }
    }
}


//class VMF(private val aServ: Provider.Service) : ViewModelProvider.NewInstanceFactory() {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return when {
//            modelClass.isAssignableFrom(vModel::class.java) -> vModel(aServ) as T
//            else -> throw java.lang.IllegalArgumentException("Uncaused error")
//        }
//    }
//}
