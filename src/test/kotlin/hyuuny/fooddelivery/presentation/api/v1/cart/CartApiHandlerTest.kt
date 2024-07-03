package hyuuny.fooddelivery.presentation.api.v1.cart

import AddItemAndOptionRequest
import AddItemToCartRequest
import UpdateCartItemOptionsRequest
import UpdateCartItemQuantityRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.cart.CartItemOptionUseCase
import hyuuny.fooddelivery.application.cart.CartItemUseCase
import hyuuny.fooddelivery.application.cart.CartUseCase
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.application.store.StoreUseCase
import hyuuny.fooddelivery.common.constant.DeliveryType
import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.domain.cart.Cart
import hyuuny.fooddelivery.domain.cart.CartItem
import hyuuny.fooddelivery.domain.cart.CartItemOption
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.option.Option
import hyuuny.fooddelivery.domain.store.Store
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartItemOptionResponse
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartItemResponse
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartResponse
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.http.MediaType
import java.time.LocalDateTime

class CartApiHandlerTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var useCase: CartUseCase

    @MockkBean
    private lateinit var cartItemUseCase: CartItemUseCase

    @MockkBean
    private lateinit var cartItemOptionUseCase: CartItemOptionUseCase

    @MockkBean
    private lateinit var menuUseCase: MenuUseCase

    @MockkBean
    private lateinit var storeUseCase: StoreUseCase

    @MockkBean
    private lateinit var optionUseCase: OptionUseCase

    @DisplayName("사용자는 장바구니에 메뉴를 추가할 수 있다.")
    @Test
    fun addItemToCart() {
        val userId = 1L
        val cartId = 1L
        val cartItemId = 1L
        val cartItemOptionId = 1L
        val menuId = 1L
        val optionId = 1L
        val storeId = 37L

        val request = AddItemToCartRequest(
            storeId = storeId,
            item = AddItemAndOptionRequest(
                menuId = menuId,
                quantity = 1,
                optionIds = listOf(1L, 2L)
            )
        )
        val cart = generateCart(cartId, userId, storeId)
        val cartItem = generateCartItem(cartItemId, cartId, menuId)
        val menu = generateMenu(menuId)
        val itemOptions = generateCartItemOptions(cartItemOptionId, cartItemId, optionId)
        val options = generateOptions(optionId)
        val store = generateStore(storeId)

        coEvery { storeUseCase.getStore(any()) } returns store
        coEvery { useCase.addItemToCart(any(), any(), any()) } returns cart
        coEvery { cartItemUseCase.getAllByCartId(cartId) } returns listOf(cartItem)
        coEvery { menuUseCase.getAllByIds(listOf(cartItem.menuId)) } returns listOf(menu)
        coEvery { cartItemOptionUseCase.getAllByCartItemIds(listOf(cartItem.id!!)) } returns itemOptions
        coEvery { optionUseCase.getAllByIds(itemOptions.map { it.optionId }) } returns options

        val expectedItemWithOptionPrice = menu.price + options.sumOf { it.price } * cartItem.quantity
        val expectedTotalPrice = menu.price + options.sumOf { it.price } * cartItem.quantity
        val expectedResponse = CartResponse(
            id = cartId,
            userId = userId,
            storeId = storeId,
            items = listOf(
                CartItemResponse(
                    id = cartItemId,
                    cartId = cartId,
                    menuId = menuId,
                    menuName = menu.name,
                    imageUrl = menu.imageUrl,
                    quantity = 1,
                    price = menu.price,
                    options = options.mapIndexed { index, option ->
                        CartItemOptionResponse(
                            id = index.toLong() + 1,
                            cartItemId = cartItemId,
                            optionId = option.id!!,
                            optionName = option.name,
                            price = option.price,
                        )
                    },
                    itemWithOptionsPrice = menu.price + options.sumOf { it.price } * cartItem.quantity
                )
            ),
            totalPrice = expectedTotalPrice,
            deliveryFee = 0
        )

        webTestClient.post().uri("/api/v1/users/$userId/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.items").isArray
            .jsonPath("$.items[0].id").isEqualTo(expectedResponse.items[0].id)
            .jsonPath("$.items[0].menuId").isEqualTo(expectedResponse.items[0].menuId)
            .jsonPath("$.items[0].menuName").isEqualTo(expectedResponse.items[0].menuName)
            .jsonPath("$.items[0].imageUrl").isEqualTo(expectedResponse.items[0].imageUrl!!)
            .jsonPath("$.items[0].quantity").isEqualTo(expectedResponse.items[0].quantity)
            .jsonPath("$.items[0].price").isEqualTo(expectedResponse.items[0].price)
            .jsonPath("$.items[0].options").isArray
            .jsonPath("$.items[0].options[0].id").isEqualTo(expectedResponse.items[0].options[0].id)
            .jsonPath("$.items[0].options[0].optionName").isEqualTo(expectedResponse.items[0].options[0].optionName)
            .jsonPath("$.items[0].options[1].id").isEqualTo(expectedResponse.items[0].options[1].id)
            .jsonPath("$.items[0].options[1].optionName").isEqualTo(expectedResponse.items[0].options[1].optionName)
            .jsonPath("$.items[0].itemWithOptionsPrice").isEqualTo(expectedItemWithOptionPrice)
            .jsonPath("$.totalPrice").isEqualTo(expectedResponse.totalPrice)
            .jsonPath("$.deliveryFee").isEqualTo(expectedResponse.deliveryFee)
    }

    @DisplayName("사용자는 장바구니에 담긴 메뉴를 조회할 수 있다.")
    @Test
    fun getCart() {
        val userId = 1L
        val cartId = 1L
        val cartItemId = 1L
        val cartItemOptionId = 1L
        val menuId = 1L
        val optionId = 1L
        val storeId = 37L

        val cart = generateCart(cartId, userId, storeId)
        val cartItem = generateCartItem(cartItemId, cartId, menuId)
        val menu = generateMenu(menuId)
        val itemOptions = generateCartItemOptions(cartItemOptionId, cartItemId, optionId)
        val options = generateOptions(optionId)

        coEvery { useCase.getOrInsertCart(any()) } returns cart
        coEvery { cartItemUseCase.getAllByCartId(any()) } returns listOf(cartItem)
        coEvery { menuUseCase.getAllByIds(any()) } returns listOf(menu)
        coEvery { cartItemOptionUseCase.getAllByCartItemIds(any()) } returns itemOptions
        coEvery { optionUseCase.getAllByIds(any()) } returns options

        val expectedItemWithOptionPrice = menu.price + options.sumOf { it.price } * cartItem.quantity
        val cartItemResponses = listOf(
            CartItemResponse(
                id = cartItemId,
                cartId = cartId,
                menuId = menuId,
                menuName = menu.name,
                imageUrl = menu.imageUrl,
                quantity = 1,
                price = menu.price,
                options = options.mapIndexed { index, option ->
                    CartItemOptionResponse(
                        id = index.toLong() + 1,
                        cartItemId = cartItemId,
                        optionId = option.id!!,
                        optionName = option.name,
                        price = option.price,
                    )
                },
                itemWithOptionsPrice = menu.price + options.sumOf { it.price } * cartItem.quantity
            )
        )
        val expectedResponse = CartResponse(
            id = cartId,
            userId = cart.userId,
            storeId = storeId,
            items = cartItemResponses,
            totalPrice = cartItemResponses.sumOf { it.itemWithOptionsPrice },
            deliveryFee = 0
        )

        webTestClient.get().uri("/api/v1/users/$userId/carts")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.storeId").isEqualTo(expectedResponse.storeId!!)
            .jsonPath("$.items").isArray
            .jsonPath("$.items[0].id").isEqualTo(expectedResponse.items[0].id)
            .jsonPath("$.items[0].menuId").isEqualTo(expectedResponse.items[0].menuId)
            .jsonPath("$.items[0].menuName").isEqualTo(expectedResponse.items[0].menuName)
            .jsonPath("$.items[0].imageUrl").isEqualTo(expectedResponse.items[0].imageUrl!!)
            .jsonPath("$.items[0].quantity").isEqualTo(expectedResponse.items[0].quantity)
            .jsonPath("$.items[0].price").isEqualTo(expectedResponse.items[0].price)
            .jsonPath("$.items[0].options").isArray
            .jsonPath("$.items[0].options[0].id").isEqualTo(expectedResponse.items[0].options[0].id)
            .jsonPath("$.items[0].options[0].optionName").isEqualTo(expectedResponse.items[0].options[0].optionName)
            .jsonPath("$.items[0].options[1].id").isEqualTo(expectedResponse.items[0].options[1].id)
            .jsonPath("$.items[0].options[1].optionName").isEqualTo(expectedResponse.items[0].options[1].optionName)
            .jsonPath("$.items[0].itemWithOptionsPrice").isEqualTo(expectedItemWithOptionPrice)
            .jsonPath("$.totalPrice").isEqualTo(expectedResponse.totalPrice)
            .jsonPath("$.deliveryFee").isEqualTo(expectedResponse.deliveryFee)
    }

    @DisplayName("장바구니가 없던 사용자가 장바구니를 조회하면 텅빈 장바구니가 보여진다.")
    @Test
    fun getCart_null_insert() {
        val userId = 1L
        val cartId = 1L
        val storeId = 37L

        val cart = generateCart(cartId, userId, storeId)

        coEvery { useCase.getOrInsertCart(any()) } returns cart
        coEvery { cartItemUseCase.getAllByCartId(cartId) } returns emptyList()
        coEvery { menuUseCase.getAllByIds(any()) } returns emptyList()
        coEvery { cartItemOptionUseCase.getAllByCartItemIds(any()) } returns emptyList()
        coEvery { optionUseCase.getAllByIds(any()) } returns emptyList()

        val expectedResponse = CartResponse(
            id = cartId,
            userId = cart.userId,
            storeId = cart.storeId,
            items = emptyList(),
            totalPrice = 0,
            deliveryFee = 0
        )

        webTestClient.get().uri("/api/v1/users/$userId/carts")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.storeId").isEqualTo(expectedResponse.storeId!!)
            .jsonPath("$.items").isEmpty
            .jsonPath("$.totalPrice").isEqualTo(expectedResponse.totalPrice)
            .jsonPath("$.deliveryFee").isEqualTo(expectedResponse.deliveryFee)
    }

    @DisplayName("장바구니 품목의 수량을 변경할 수 있다.")
    @Test
    fun updateCartItemQuantity() {
        val userId = 1L
        val cartId = 1L
        val cartItemId = 1L
        val cartItemOptionId = 1L
        val menuId = 1L
        val optionId = 1L
        val storeId = 37L

        val updatedQuantity = 5
        val request = UpdateCartItemQuantityRequest(quantity = updatedQuantity)
        val cart = generateCart(cartId, userId, storeId)
        val cartItem = generateCartItem(cartItemId, cartId, menuId, updatedQuantity)
        val menu = generateMenu(menuId)
        val itemOptions = generateCartItemOptions(cartItemOptionId, cartItemId, optionId)
        val options = generateOptions(optionId)

        coEvery { useCase.updateCartItemQuantity(any(), any(), any()) } returns Unit
        coEvery { useCase.getOrInsertCart(any()) } returns cart
        coEvery { cartItemUseCase.getAllByCartId(any()) } returns listOf(cartItem)
        coEvery { menuUseCase.getAllByIds(any()) } returns listOf(menu)
        coEvery { cartItemOptionUseCase.getAllByCartItemIds(any()) } returns itemOptions
        coEvery { optionUseCase.getAllByIds(any()) } returns options

        val expectedItemWithOptionPrice = (menu.price + options.sumOf { it.price }) * cartItem.quantity
        val cartItemResponses = listOf(
            CartItemResponse(
                id = cartItemId,
                cartId = cartId,
                menuId = menuId,
                menuName = menu.name,
                imageUrl = menu.imageUrl,
                quantity = updatedQuantity,
                price = menu.price,
                options = options.mapIndexed { index, option ->
                    CartItemOptionResponse(
                        id = index.toLong() + 1,
                        cartItemId = cartItemId,
                        optionId = option.id!!,
                        optionName = option.name,
                        price = option.price,
                    )
                },
                itemWithOptionsPrice = expectedItemWithOptionPrice
            )
        )
        val expectedResponse = CartResponse(
            id = cartId,
            userId = cart.userId,
            storeId = cart.storeId,
            items = cartItemResponses,
            totalPrice = cartItemResponses.sumOf { it.itemWithOptionsPrice },
            deliveryFee = 0
        )

        webTestClient.put().uri("/api/v1/users/$userId/carts/$cartId/cart-items/$cartItemId")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.storeId").isEqualTo(expectedResponse.storeId!!)
            .jsonPath("$.items").isArray
            .jsonPath("$.items[0].id").isEqualTo(expectedResponse.items[0].id)
            .jsonPath("$.items[0].menuId").isEqualTo(expectedResponse.items[0].menuId)
            .jsonPath("$.items[0].menuName").isEqualTo(expectedResponse.items[0].menuName)
            .jsonPath("$.items[0].imageUrl").isEqualTo(expectedResponse.items[0].imageUrl!!)
            .jsonPath("$.items[0].quantity").isEqualTo(updatedQuantity)
            .jsonPath("$.items[0].price").isEqualTo(expectedResponse.items[0].price)
            .jsonPath("$.items[0].options").isArray
            .jsonPath("$.items[0].options[0].id").isEqualTo(expectedResponse.items[0].options[0].id)
            .jsonPath("$.items[0].options[0].optionName").isEqualTo(expectedResponse.items[0].options[0].optionName)
            .jsonPath("$.items[0].options[1].id").isEqualTo(expectedResponse.items[0].options[1].id)
            .jsonPath("$.items[0].options[1].optionName").isEqualTo(expectedResponse.items[0].options[1].optionName)
            .jsonPath("$.items[0].itemWithOptionsPrice").isEqualTo(expectedItemWithOptionPrice)
            .jsonPath("$.totalPrice").isEqualTo(expectedResponse.totalPrice)
            .jsonPath("$.deliveryFee").isEqualTo(expectedResponse.deliveryFee)
    }

    @DisplayName("장바구니에 담긴 품목의 옵션을 변경할 수 있다.")
    @Test
    fun updateCartItemOptions() {
        val userId = 1L
        val cartId = 1L
        val cartItemId = 1L
        val cartItemOptionId = 1L
        val menuId = 1L
        val newOptionId = 13L
        val storeId = 37L

        val cart = generateCart(cartId, userId, storeId)
        val cartItem = generateCartItem(cartItemId, cartId, menuId)
        val menu = generateMenu(menuId)

        val now = LocalDateTime.now()
        val newOptions = listOf(
            Option(newOptionId, 1L, "후라이드 + 양념", 1000, now, now),
            Option(newOptionId + 1, 2L, "치즈볼", 5000, now, now),
            Option(newOptionId + 2, 2L, "양념 소스", 500, now, now),
        )
        val newItemOptions = listOf(
            CartItemOption(cartItemOptionId, cartItemId, newOptionId, now),
            CartItemOption(cartItemOptionId + 1, cartItemId, newOptionId + 1, now),
            CartItemOption(cartItemOptionId + 2, cartItemId, newOptionId + 2, now),
        )
        val request = UpdateCartItemOptionsRequest(optionIds = newOptions.mapNotNull { it.id })

        coEvery { useCase.updateCartItemOptions(any(), any(), any()) } returns Unit
        coEvery { useCase.getOrInsertCart(any()) } returns cart
        coEvery { cartItemUseCase.getAllByCartId(any()) } returns listOf(cartItem)
        coEvery { menuUseCase.getAllByIds(any()) } returns listOf(menu)
        coEvery { cartItemOptionUseCase.getAllByCartItemIds(any()) } returns newItemOptions
        coEvery { optionUseCase.getAllByIds(any()) } returns newOptions

        val expectedItemWithOptionPrice = (menu.price + newOptions.sumOf { it.price }) * cartItem.quantity
        val cartItemResponses = listOf(
            CartItemResponse(
                id = cartItemId,
                cartId = cartId,
                menuId = menuId,
                menuName = menu.name,
                imageUrl = menu.imageUrl,
                quantity = 1,
                price = menu.price,
                options = newOptions.mapIndexed { index, option ->
                    CartItemOptionResponse(
                        id = index.toLong() + 1,
                        cartItemId = cartItemId,
                        optionId = option.id!!,
                        optionName = option.name,
                        price = option.price,
                    )
                },
                itemWithOptionsPrice = expectedItemWithOptionPrice
            )
        )
        val expectedResponse = CartResponse(
            id = cartId,
            userId = cart.userId,
            storeId = cart.storeId,
            items = cartItemResponses,
            totalPrice = cartItemResponses.sumOf { it.itemWithOptionsPrice },
            deliveryFee = 0,
        )

        webTestClient.put().uri("/api/v1/users/$userId/carts/$cartId/cart-items/$cartItemId/options")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.storeId").isEqualTo(expectedResponse.storeId!!)
            .jsonPath("$.items").isArray
            .jsonPath("$.items[0].id").isEqualTo(expectedResponse.items[0].id)
            .jsonPath("$.items[0].menuId").isEqualTo(expectedResponse.items[0].menuId)
            .jsonPath("$.items[0].menuName").isEqualTo(expectedResponse.items[0].menuName)
            .jsonPath("$.items[0].imageUrl").isEqualTo(expectedResponse.items[0].imageUrl!!)
            .jsonPath("$.items[0].quantity").isEqualTo(expectedResponse.items[0].quantity)
            .jsonPath("$.items[0].price").isEqualTo(expectedResponse.items[0].price)
            .jsonPath("$.items[0].options").isArray
            .jsonPath("$.items[0].options[0].id").isEqualTo(expectedResponse.items[0].options[0].id)
            .jsonPath("$.items[0].options[0].optionName").isEqualTo(expectedResponse.items[0].options[0].optionName)
            .jsonPath("$.items[0].options[1].id").isEqualTo(expectedResponse.items[0].options[1].id)
            .jsonPath("$.items[0].options[1].optionName").isEqualTo(expectedResponse.items[0].options[1].optionName)
            .jsonPath("$.items[0].options[2].id").isEqualTo(expectedResponse.items[0].options[2].id)
            .jsonPath("$.items[0].options[2].optionName").isEqualTo(expectedResponse.items[0].options[2].optionName)
            .jsonPath("$.items[0].itemWithOptionsPrice").isEqualTo(expectedItemWithOptionPrice)
            .jsonPath("$.totalPrice").isEqualTo(expectedResponse.totalPrice)
            .jsonPath("$.deliveryFee").isEqualTo(expectedResponse.deliveryFee)
    }

    @DisplayName("장바구니에 담긴 품목을 삭제할 수 있다.")
    @Test
    fun deleteCartItem() {
        val userId = 1L
        val cartId = 1L
        val cartItemId = 1L
        val storeId = 37L

        val cart = generateCart(cartId, userId, storeId)

        coEvery { useCase.deleteCartItem(any(), any()) } returns Unit
        coEvery { useCase.getOrInsertCart(any()) } returns cart
        coEvery { cartItemUseCase.getAllByCartId(any()) } returns emptyList()
        coEvery { menuUseCase.getAllByIds(any()) } returns emptyList()
        coEvery { cartItemOptionUseCase.getAllByCartItemIds(any()) } returns emptyList()
        coEvery { optionUseCase.getAllByIds(any()) } returns emptyList()

        val expectedResponse = CartResponse(
            id = cartId,
            userId = cart.userId,
            storeId = cart.storeId,
            items = emptyList(),
            totalPrice = 0,
            deliveryFee = 0
        )

        webTestClient.delete().uri("/api/v1/users/$userId/carts/$cartId/cart-items/$cartItemId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.storeId").isEqualTo(expectedResponse.storeId!!)
            .jsonPath("$.items.length()").isEqualTo(0)
            .jsonPath("$.totalPrice").isEqualTo(expectedResponse.totalPrice)
            .jsonPath("$.deliveryFee").isEqualTo(expectedResponse.deliveryFee)
    }

    @DisplayName("사용자는 특정 매장의 장바구니가 있는지 확인할 수 있다.")
    @CsvSource("ture", "false")
    @ParameterizedTest
    fun existsCartByUserIdAndStoreId(exists: Boolean) {
        val userId = 1L
        val storeId = 37L

        coEvery { useCase.existsCartByUserIdAndStoreId(any(), any()) } returns exists

        webTestClient.get().uri("/api/v1/users/$userId/carts/stores/$storeId/exists")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.exists").isEqualTo(exists)
    }

    private fun generateCart(id: Long, userId: Long, storeId: Long?): Cart {
        val now = LocalDateTime.now()
        return Cart(id = id, userId = userId, storeId = storeId, createdAt = now, updatedAt = now)
    }

    private fun generateCartItem(id: Long, cartId: Long, menuId: Long, quantity: Int = 1): CartItem {
        val now = LocalDateTime.now()
        return CartItem(
            id = id,
            cartId = cartId,
            menuId = menuId,
            quantity = quantity,
            createdAt = now,
            updatedAt = now
        )
    }

    private fun generateCartItemOptions(id: Long, cartItemId: Long, optionId: Long): List<CartItemOption> {
        val now = LocalDateTime.now()
        return listOf(
            CartItemOption(id = id, cartItemId = cartItemId, optionId = optionId, createdAt = now),
            CartItemOption(id = id + 1, cartItemId = cartItemId, optionId = optionId + 1, createdAt = now),
        )
    }

    private fun generateMenu(id: Long): Menu {
        val now = LocalDateTime.now()
        return Menu(
            id = id,
            menuGroupId = 1L,
            name = "네네치킨",
            price = 19000,
            status = MenuStatus.ON_SALE,
            popularity = true,
            imageUrl = "chicken-image-url",
            description = "맛있어요!",
            createdAt = now,
            updatedAt = now
        )
    }

    private fun generateOptions(id: Long): List<Option> {
        val now = LocalDateTime.now()
        return listOf(
            Option(
                id = id,
                optionGroupId = 1L,
                name = "후라이드 + 양념",
                price = 1000,
                createdAt = now,
                updatedAt = now
            ),
            Option(
                id = id + 1,
                optionGroupId = 1L,
                name = "치즈스틱 2EA",
                price = 2000,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    private fun generateStore(id: Long): Store {
        val now = LocalDateTime.now()
        return Store(
            id = id,
            categoryId = 1L,
            deliveryType = DeliveryType.OUTSOURCING,
            name = "BBQ",
            ownerName = "김성현",
            taxId = "123-12-12345",
            deliveryFee = 1000,
            minimumOrderAmount = 18000,
            iconImageUrl = "icon-image-url-5.jpg",
            description = "저희 업소는 100% 국내산 닭고기를 사용하며, BBQ 올리브 오일만을 사용합니다.",
            foodOrigin = "황금올리브치킨(후라이드/속안심/핫윙/블랙페퍼/레드착착/크런치 버터), 핫황금올리브치킨크리스피, 파더`s치킨(로스트 갈릭/와사비)",
            phoneNumber = "02-1234-1234",
            createdAt = now,
            updatedAt = now
        )
    }
}