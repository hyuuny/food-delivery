package hyuuny.fooddelivery.presentation.api.v1.cart

import AddItemAndOptionRequest
import AddItemToCartRequest
import UpdateCartItemQuantityRequest
import com.ninjasquad.springmockk.MockkBean
import hyuuny.fooddelivery.application.cart.CartItemOptionUseCase
import hyuuny.fooddelivery.application.cart.CartItemUseCase
import hyuuny.fooddelivery.application.cart.CartUseCase
import hyuuny.fooddelivery.application.menu.MenuUseCase
import hyuuny.fooddelivery.application.option.OptionUseCase
import hyuuny.fooddelivery.common.constant.MenuStatus
import hyuuny.fooddelivery.domain.cart.Cart
import hyuuny.fooddelivery.domain.cart.CartItem
import hyuuny.fooddelivery.domain.cart.CartItemOption
import hyuuny.fooddelivery.domain.menu.Menu
import hyuuny.fooddelivery.domain.option.Option
import hyuuny.fooddelivery.presentation.admin.v1.BaseIntegrationTest
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartItemOptionResponse
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartItemResponse
import hyuuny.fooddelivery.presentation.api.v1.cart.response.CartResponse
import io.mockk.coEvery
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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

        val request = AddItemToCartRequest(
            item = AddItemAndOptionRequest(
                menuId = menuId,
                quantity = 1,
                optionIds = listOf(1L, 2L)
            )
        )
        val cart = generateCart(cartId, userId)
        val cartItem = generateCartItem(cartItemId, cartId, menuId)
        val menu = generateMenu(menuId)
        val itemOptions = generateCartItemOptions(cartItemOptionId, cartItemId, optionId)
        val options = generateOptions(optionId)

        coEvery { useCase.addItemToCart(userId, any()) } returns cart
        coEvery { cartItemUseCase.getAllByCartId(cartId) } returns listOf(cartItem)
        coEvery { menuUseCase.getAllByIds(listOf(cartItem.menuId)) } returns listOf(menu)
        coEvery { cartItemOptionUseCase.getAllByCartItemIds(listOf(cartItem.id!!)) } returns itemOptions
        coEvery { optionUseCase.getAllByIds(itemOptions.map { it.optionId }) } returns options

        val expectedItemWithOptionPrice = menu.price + options.sumOf { it.price } * cartItem.quantity
        val expectedTotalPrice = menu.price + options.sumOf { it.price } * cartItem.quantity
        val expectedResponse = CartResponse(
            id = cartId,
            userId = userId,
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
            totalPrice = expectedTotalPrice
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

        val cart = generateCart(cartId, userId)
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
            items = cartItemResponses,
            totalPrice = cartItemResponses.sumOf { it.itemWithOptionsPrice }
        )

        webTestClient.get().uri("/api/v1/users/$userId/carts")
            .accept(MediaType.APPLICATION_JSON)
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
    }

    @DisplayName("장바구니가 없던 사용자가 장바구니를 조회하면 텅빈 장바구니가 보여진다.")
    @Test
    fun getCart_null_insert() {
        val userId = 1L
        val cartId = 1L

        val cart = generateCart(cartId, userId)

        coEvery { useCase.getOrInsertCart(any()) } returns cart
        coEvery { cartItemUseCase.getAllByCartId(cartId) } returns emptyList()
        coEvery { menuUseCase.getAllByIds(any()) } returns emptyList()
        coEvery { cartItemOptionUseCase.getAllByCartItemIds(any()) } returns emptyList()
        coEvery { optionUseCase.getAllByIds(any()) } returns emptyList()

        val expectedResponse = CartResponse(
            id = cartId,
            userId = cart.userId,
            items = emptyList(),
            totalPrice = 0
        )

        webTestClient.get().uri("/api/v1/users/$userId/carts")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(::println)
            .jsonPath("$.id").isEqualTo(expectedResponse.id)
            .jsonPath("$.userId").isEqualTo(expectedResponse.userId)
            .jsonPath("$.items").isEmpty
            .jsonPath("$.totalPrice").isEqualTo(expectedResponse.totalPrice)
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

        val updatedQuantity = 5
        val request = UpdateCartItemQuantityRequest(quantity = updatedQuantity)
        val cart = generateCart(cartId, userId)
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
            items = cartItemResponses,
            totalPrice = cartItemResponses.sumOf { it.itemWithOptionsPrice }
        )

        webTestClient.put().uri("/api/v1/users/$userId/carts/$cartId/cart-item/$cartItemId")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
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
            .jsonPath("$.items[0].quantity").isEqualTo(updatedQuantity)
            .jsonPath("$.items[0].price").isEqualTo(expectedResponse.items[0].price)
            .jsonPath("$.items[0].options").isArray
            .jsonPath("$.items[0].options[0].id").isEqualTo(expectedResponse.items[0].options[0].id)
            .jsonPath("$.items[0].options[0].optionName").isEqualTo(expectedResponse.items[0].options[0].optionName)
            .jsonPath("$.items[0].options[1].id").isEqualTo(expectedResponse.items[0].options[1].id)
            .jsonPath("$.items[0].options[1].optionName").isEqualTo(expectedResponse.items[0].options[1].optionName)
            .jsonPath("$.items[0].itemWithOptionsPrice").isEqualTo(expectedItemWithOptionPrice)
            .jsonPath("$.totalPrice").isEqualTo(expectedResponse.totalPrice)
    }

    private fun generateCart(id: Long, userId: Long): Cart {
        val now = LocalDateTime.now()
        return Cart(id = id, userId = userId, createdAt = now, updatedAt = now)
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
                name = "간장 + 양념",
                price = 2000,
                createdAt = now,
                updatedAt = now
            )
        )
    }
}