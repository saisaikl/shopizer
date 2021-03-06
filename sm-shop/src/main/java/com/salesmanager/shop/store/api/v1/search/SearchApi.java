package com.salesmanager.shop.store.api.v1.search;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.ValueList;
import com.salesmanager.shop.model.catalog.SearchProductList;
import com.salesmanager.shop.model.catalog.SearchProductRequest;
import com.salesmanager.shop.store.controller.search.facade.SearchFacade;
import com.salesmanager.shop.store.controller.store.facade.StoreFacade;
import com.salesmanager.shop.utils.LanguageUtils;

/**
 * Api for searching shopizer catalog based on search term
 * when filtering products based on product attribute is required, see /api/v1/product
 * @author c.samson
 *
 */
@Controller
@RequestMapping("/api/v1")
public class SearchApi {
	
	@Inject
	private SearchFacade searchFacade;
	
	@Inject
	private StoreFacade storeFacade;
	
	@Inject
	private LanguageUtils languageUtils;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchApi.class);
	
	
	/**
	 * Search products from underlying elastic search
	 * @param searchRequest
	 * @param lang nothing or en or fr ...
	 * @param request
	 * @param response
	 * @return SearchProductList
	 * @throws Exception
	 */
    @ResponseStatus(HttpStatus.OK)
	@RequestMapping( value="/search", method=RequestMethod.POST)
	public @ResponseBody SearchProductList search(@RequestBody SearchProductRequest searchRequest, @RequestParam(value = "lang", required=false) String lang, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try {
			
			MerchantStore merchantStore = storeFacade.getByCode(request);
			Language language = languageUtils.getRESTLanguage(request, merchantStore);			
			SearchProductList productList = searchFacade.search(merchantStore, language, searchRequest);
			
			return productList;
			
		} catch (Exception e) {
			LOGGER.error("Error while searching products",e);
			try {
				response.sendError(503, "Error while searching products " + e.getMessage());
			} catch (Exception ignore) {
			}

		}
		return null;
	}
    
    @ResponseStatus(HttpStatus.OK)
	@RequestMapping( value="/search/autocomplete", method=RequestMethod.POST)
	public @ResponseBody ValueList autocomplete(@RequestBody SearchProductRequest searchRequest, @RequestParam(value = "lang", required=false) String lang, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try {
			
			MerchantStore merchantStore = storeFacade.getByCode(request);
			Language language = languageUtils.getRESTLanguage(request, merchantStore);			
			ValueList keywords = searchFacade.autocompleteRequest(searchRequest.getQuery(), merchantStore, language);
			
			return keywords;
			
		} catch (Exception e) {
			LOGGER.error("Error while autocomplete products",e);
			try {
				response.sendError(503, "Error while autocomplete products " + e.getMessage());
			} catch (Exception ignore) {
			}

		}
		return null;
	}

}
