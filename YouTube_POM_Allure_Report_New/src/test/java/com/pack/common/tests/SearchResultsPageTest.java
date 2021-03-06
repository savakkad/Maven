package com.pack.common.tests;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.pack.base.TestBaseSetup;
import com.pack.common.pageobjects.HomePage;
import com.pack.common.pageobjects.SearchResultsPage;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Feature("SearchResultsPage")
public class SearchResultsPageTest extends TestBaseSetup{
	private WebDriver driver;

	private HomePage homePage;
	private SearchResultsPage searchResultsPage;
		
		@BeforeMethod
		public void setUp() {
			driver=getDriver();
		}
			
		@Severity(SeverityLevel.NORMAL)
		@Description("Verify Search Results functionality")
		@Story("Verify Search Results functionality")
		@Test(description="Verify Search Results functionality")
		public void verifySearchResults() throws Exception {
			String TCName = Thread.currentThread().getStackTrace()[1].getMethodName();
			homePage = new HomePage(driver);
			homePage.verifySearchIcon();
			homePage.clickSearchIcon();
			homePage.enterValue(TCName);
			homePage.clickSearch();
			
			searchResultsPage = new SearchResultsPage(driver);
			searchResultsPage.verifySearchResults("Motorola");
		}
}
