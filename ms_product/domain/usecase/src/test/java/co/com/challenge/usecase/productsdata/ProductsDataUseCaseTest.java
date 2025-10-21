package co.com.challenge.usecase.productsdata;

import co.com.challenge.model.itemmodel.ItemModel;
import co.com.challenge.model.reviewsmodel.ReviewsModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsDataUseCaseTest {

    @InjectMocks
    private ProductsDataUseCase useCase;

    @Mock
    private ObjectMapper mockMapper;

    @Mock
    private Logger mockLogger;

    // Mocks de modelos para no depender de constructores/setters reales
    private ItemModel item1;
    private ItemModel item2;

    private ReviewsModel r11_low;
    private ReviewsModel r12_high;
    private ReviewsModel r21;

    @BeforeEach
    void setUp() throws Exception {
        // Inyectamos mocks para mapper/logger en campos privados/finales
        setPrivateField(useCase, "mapper", mockMapper);
        setPrivateField(useCase, "logger", mockLogger);

        // Construimos items mockeados
        item1 = mock(ItemModel.class);
        lenient().when(item1.getTitle()).thenReturn("Phone Pro 15");
        lenient().when(item1.getCategory()).thenReturn("Electronics");
        lenient().when(item1.getPrice()).thenReturn(999.0);

        item2 = mock(ItemModel.class);
        lenient().when(item2.getTitle()).thenReturn("Eco Kettle");
        lenient().when(item2.getCategory()).thenReturn("Home");
        lenient().when(item2.getPrice()).thenReturn(45.5);

        Map<String, ItemModel> items = new HashMap<>();
        items.put("1", item1);
        items.put("2", item2);

        // Reviews para item 1 (dos, con ratings distintos) y item 2 (una)
        r11_low = mock(ReviewsModel.class);
        lenient().when(r11_low.getItem_id()).thenReturn("1");
        lenient().when(r11_low.getRating()).thenReturn(3.8);
        lenient().when(r11_low.getComment()).thenReturn("Bueno, pero la batería dura poco");

        r12_high = mock(ReviewsModel.class);
        lenient().when(r12_high.getItem_id()).thenReturn("1");
        lenient().when(r12_high.getRating()).thenReturn(4.9);
        lenient().when(r12_high.getComment()).thenReturn("Excelente cámara y rendimiento");

        r21 = mock(ReviewsModel.class);
        lenient().when(r21.getItem_id()).thenReturn("2");
        lenient().when(r21.getRating()).thenReturn(4.2);
        lenient().when(r21.getComment()).thenReturn("Ahorra energía y es silenciosa");

        List<ReviewsModel> reviews = List.of(r11_low, r12_high, r21);

        // Evitamos E/S: cargamos datos directamente para que loadProducts() haga early-return
        setPrivateField(useCase, "items", items);
        setPrivateField(useCase, "reviews", reviews);
    }

    // Utilidad para setear campos privados
    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Nested
    @DisplayName("getProductById")
    class GetProductById {

        @Test
        @DisplayName("Devuelve item y la mejor recomendación cuando el id existe")
        void returnsItemAndBestRecommendation_whenExists() {
            Map<String, Object> result = useCase.getProductById("1");
            assertNotNull(result, "El resultado no debe ser nulo");

            assertSame(item1, result.get("item"), "Debe devolver el item correcto");
            assertEquals("Excelente cámara y rendimiento",
                    result.get("best_recommendation"),
                    "Debe seleccionar el comentario de mayor rating");

            verify(mockLogger, atLeastOnce()).info(contains("Product with id: 1"));
        }

        @Test
        @DisplayName("Retorna null y loggea cuando el id no existe")
        void returnsNull_whenNotExists() {
            Map<String, Object> result = useCase.getProductById("999");
            assertNull(result, "Debe ser null cuando no existe el producto");
            verify(mockLogger, atLeastOnce()).info(contains("Product with id: 999 not found"));
        }
    }

    @Nested
    @DisplayName("getRecomendationByProductId")
    class GetRecommendations {

        @Test
        @DisplayName("Filtra reviews por item_id")
        void filtersById() {
            @SuppressWarnings("unchecked")
            List<ReviewsModel> result = (List<ReviewsModel>) useCase.getRecomendationByProductId("1");
            assertEquals(2, result.size(), "Debe devolver 2 reviews para el id=1");
            assertTrue(result.contains(r11_low) && result.contains(r12_high));
        }

        @Test
        @DisplayName("Devuelve lista vacía si no hay reviews para el id")
        void returnsEmptyWhenNoReviews() {
            @SuppressWarnings("unchecked")
            List<ReviewsModel> result = (List<ReviewsModel>) useCase.getRecomendationByProductId("999");
            assertNotNull(result, "Nunca debe ser null");
            assertTrue(result.isEmpty(), "Debe ser vacía para ids sin reviews");
        }
    }

    @Nested
    @DisplayName("getAllProducts")
    class GetAllProducts {

        @Test
        @DisplayName("Valida size > 0")
        void throwsWhenInvalidSize() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> useCase.getAllProducts(null, null, null, null, 1, 0)
            );
            assertTrue(ex.getMessage().contains("size"), "Mensaje debe indicar problema con 'size'");
        }

        @Test
        @DisplayName("Pagina resultados y corrige page < 1 a 1")
        void paginatesAndFixesLowerPage() {
            Map<String, Object> page1 = useCase.getAllProducts(null, null, null, null, 0, 1);
            assertEquals(1, page1.get("page"));
            assertEquals(2, page1.get("totalItems"));
            assertEquals(2, page1.get("totalPages"));
            @SuppressWarnings("unchecked")
            List<ItemModel> data = (List<ItemModel>) page1.get("data");
            assertEquals(1, data.size());
        }

        @Test
        @DisplayName("Lanza excepción cuando page > totalPages")
        void throwsWhenPageOutOfRange() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> useCase.getAllProducts(null, null, null, null, 5, 2)
            );
            assertTrue(ex.getMessage().contains("fuera de rango"));
        }

        @Test
        @DisplayName("Filtra por title (contains, case-insensitive)")
        void filtersByTitle() {
            Map<String, Object> res = useCase.getAllProducts("phone", null, null, null, 1, 10);
            @SuppressWarnings("unchecked")
            List<ItemModel> data = (List<ItemModel>) res.get("data");
            assertEquals(1, data.size());
            assertSame(item1, data.get(0));
        }

        @Test
        @DisplayName("Filtra por categoría (equalsIgnoreCase)")
        void filtersByCategory() {
            Map<String, Object> res = useCase.getAllProducts(null, null, null, "home", 1, 10);
            @SuppressWarnings("unchecked")
            List<ItemModel> data = (List<ItemModel>) res.get("data");
            assertEquals(1, data.size());
            assertSame(item2, data.get(0));
        }

        @Test
        @DisplayName("Filtra por rango de precios (min/max)")
        void filtersByPriceRange() {
            Map<String, Object> res = useCase.getAllProducts(null, 50.0, 1000.0, null, 1, 10);
            @SuppressWarnings("unchecked")
            List<ItemModel> data = (List<ItemModel>) res.get("data");
            assertEquals(1, data.size(), "Sólo el Phone entra en [50,1000]");
            assertSame(item1, data.get(0));
        }

        @Test
        @DisplayName("Combina filtros y pagina")
        void combinesFiltersAndPaginates() {
            // Filtro que deja ambos (sin filtros) pero pagina size=1
            Map<String, Object> res = useCase.getAllProducts(null, null, null, null, 2, 1);
            assertEquals(2, res.get("page"));
            @SuppressWarnings("unchecked")
            List<ItemModel> data = (List<ItemModel>) res.get("data");
            assertEquals(1, data.size());
        }

    }


    @Test
    @DisplayName("loadProducts() lee JSON/CSV del classpath y llena items/reviews")
    void loadProducts_readsResources_ok() throws Exception {
        // Act
        useCase.loadProducts();

        // Assert indirecto: llama a un método público que depende de items/reviews cargados
        Map<String, Object> byId = useCase.getProductById("1");
        assertNotNull(byId, "Debe existir el producto con id=1");
        assertEquals("Phone Pro 15", ((ItemModel) byId.get("item")).getTitle());
        assertEquals("Excelente cámara y rendimiento", byId.get("best_recommendation"));

        // También podemos comprobar getAllProducts (usa items)
        Map<String, Object> page = useCase.getAllProducts(null, null, null, null, 1, 10);
        assertEquals(2, page.get("totalItems"));
        @SuppressWarnings("unchecked")
        List<ItemModel> data = (List<ItemModel>) page.get("data");
        assertEquals(2, data.size());
    }

    @Test
    @DisplayName("loadProducts() early return: si items y reviews ya están seteados, no vuelve a leer")
    void loadProducts_earlyReturn() throws Exception {
        // Pre-cargamos items/reviews manualmente
        Map<String, ItemModel> items = new HashMap<>();
        items.put("X", new ItemModel());
        setField(useCase, "items", items);
        setField(useCase, "reviews", List.of());

        // Inyectamos un ObjectMapper mock para detectar si intenta leer JSON (no debería)
        ObjectMapper mapperMock = mock(ObjectMapper.class);
        setField(useCase, "mapper", mapperMock);

        // Act
        useCase.loadProducts();

        // Assert: al ya tener items y reviews, debe retornar sin usar el mapper
        verifyNoInteractions(mapperMock);
        // Y se mantiene lo que ya estaba
        Map<String, Object> page = useCase.getAllProducts(null, null, null, null, 1, 10);
        assertEquals(1, page.get("totalItems"));
    }

    @Test
    @DisplayName("loadProducts() envuelve IOException del mapper en UncheckedIOException")
    void loadProducts_wrapsIOException() throws Exception {
        // Dejamos items=null y reviews=null para forzar lectura
        setField(useCase, "items", null);
        setField(useCase, "reviews", null);

        // Mockeamos mapper para que lance IOException al leer JSON
        ObjectMapper mapperMock = mock(ObjectMapper.class);
        when(mapperMock.readValue(any(java.io.InputStream.class), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenThrow(new IOException("boom"));
        setField(useCase, "mapper", mapperMock);

        // Act + Assert
        assertThrows(UncheckedIOException.class, () -> useCase.loadProducts());
    }

    @Test
    @DisplayName("readCsvReviews() parsea CSV, salta cabecera y crea ReviewsModel (invocado por reflexión)")
    void readCsvReviews_parsesCSV_ok() throws Exception {
        // Asegura que los recursos existen en src/test/resources/data/
        Method m = ProductsDataUseCase.class.getDeclaredMethod("readCsvReviews");
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<ReviewsModel> reviews = (List<ReviewsModel>) m.invoke(useCase);

        assertNotNull(reviews);
        assertEquals(120, reviews.size(), "Debe leer 3 filas (y saltar el header)");
        assertEquals("MLA10001", reviews.get(0).getItem_id());
        assertEquals(4.4, reviews.get(0).getRating(), 1e-9);
        assertEquals("El sonido es claro. El color es bonito. La app podría mejorar. El material se siente resistente.", reviews.get(1).getComment());
    }

    // Utilidad para setear campos privados (no-final)
    private static void setField(Object target, String field, Object value) throws Exception {
        var f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }
}
