package ru.backspark.testing.service.impl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.backspark.testing.exception.InvalidDataException;
import ru.backspark.testing.exception.ObjectNotFoundException;
import ru.backspark.testing.model.dto.SocksDto;
import ru.backspark.testing.model.dto.SocksFilterParams;
import ru.backspark.testing.service.interfaces.SocksService;

import static org.mockito.Mockito.when;

@Transactional
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class SocksServiceImplTest  {

    @Autowired
    private SocksService socksService;
    private static SocksDto socksDtoBlue;
    private static SocksDto socksDtoRed;

    @BeforeClass
    public static void init() {
        socksDtoBlue = SocksDto.builder()
                .color("синий")
                .count(20)
                .cottonPercentContent(50)
                .build();
        socksDtoRed = SocksDto.builder()
                .color("красный")
                .count(10)
                .cottonPercentContent(100)
                .build();
    }

    @Test
    public void whenSuccessCalculateIncome() {
        socksService.calculateIncome(socksDtoBlue);
        var result = socksService.findWithFilter(new SocksFilterParams()).get(0);
        Assert.assertEquals(socksDtoBlue.getCount(), result.getCount());
        Assert.assertEquals(socksDtoBlue.getColor(), result.getColor());
        Assert.assertEquals(socksDtoBlue.getCottonPercentContent(), result.getCottonPercentContent());
    }

    @Test
    public void whenSuccessCalculateOutcome() {
        socksService.calculateIncome(socksDtoBlue);
        socksService.calculateOutcome(socksDtoBlue);
        var result = socksService.findWithFilter(new SocksFilterParams()).get(0);
        Assert.assertEquals((Integer) 0, result.getCount());
        Assert.assertEquals(socksDtoBlue.getColor(), result.getColor());
        Assert.assertEquals(socksDtoBlue.getCottonPercentContent(), result.getCottonPercentContent());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void whenCalculateOutcomeAndNotFound() {
        socksService.calculateOutcome(socksDtoRed);
    }

    @Test(expected = InvalidDataException.class)
    public void whenCalculateOutcomeAndExceedsTheRemainingStock() {
        socksService.calculateIncome(socksDtoRed);
        socksDtoRed.setCount(30);
        socksService.calculateOutcome(socksDtoRed);
    }

    @Test
    public void whenSuccessUpdate() {
        var saved = socksService.calculateIncome(socksDtoBlue);
        var id = saved.getId();
        socksService.update(id, socksDtoRed);
        var resultList = socksService.findWithFilter(new SocksFilterParams());
        Assert.assertEquals(1, resultList.size());
        var result = resultList.get(0);
        Assert.assertEquals(socksDtoRed.getCount(), result.getCount());
        Assert.assertEquals(socksDtoRed.getColor(), result.getColor());
        Assert.assertEquals(socksDtoRed.getCottonPercentContent(), result.getCottonPercentContent());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void whenIdNotExists() {
        socksService.update(1L, socksDtoRed);
    }

    @Test(expected = InvalidDataException.class)
    public void whenIncorrectFormat() {
        String fileName = "test.docx";
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        socksService.uploadBatch(mockFile);
    }

    @Test(expected = InvalidDataException.class)
    public void whenFileIsEmpty() {
        String fileName = "test.xlsx";
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.isEmpty()).thenReturn(true);
        socksService.uploadBatch(mockFile);
    }

    @Test
    public void whenFindWithColorFilter() {
        socksService.calculateIncome(socksDtoBlue);
        socksService.calculateIncome(socksDtoRed);
        var params = new SocksFilterParams();
        params.setColor(socksDtoBlue.getColor());
        var resultList = socksService.findWithFilter(params);
        Assert.assertEquals(1, resultList.size());
        var result = resultList.get(0);
        Assert.assertEquals(socksDtoBlue.getCount(), result.getCount());
        Assert.assertEquals(socksDtoBlue.getColor(), result.getColor());
        Assert.assertEquals(socksDtoBlue.getCottonPercentContent(), result.getCottonPercentContent());
    }

    @Test
    public void whenFindWithCottonPercentFilter() {
        socksService.calculateIncome(socksDtoBlue);
        socksService.calculateIncome(socksDtoRed);
        var params = new SocksFilterParams();
        params.setCottonPercentMax(90);
        params.setCottonPercentMin(60);
        var resultList = socksService.findWithFilter(params);
        Assert.assertTrue(resultList.isEmpty());
    }

    @Test
    public void whenSortedAscAndDesc() {
        socksService.calculateIncome(socksDtoBlue);
        socksService.calculateIncome(socksDtoRed);
        var params = new SocksFilterParams();
        var resultList = socksService.findWithFilter(params);
        params.setAscending(false);
        var resultList2 = socksService.findWithFilter(params);
        Assert.assertEquals(resultList.get(0).getColor(), socksDtoRed.getColor());
        Assert.assertEquals(resultList2.get(0).getColor(), socksDtoBlue.getColor());
    }
}