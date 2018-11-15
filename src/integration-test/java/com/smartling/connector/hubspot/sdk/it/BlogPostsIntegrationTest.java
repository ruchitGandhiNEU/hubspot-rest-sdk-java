package com.smartling.connector.hubspot.sdk.it;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotBlogPostClient;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostFilter;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager.createTokenProvider;
import static org.fest.assertions.api.Assertions.assertThat;

public class BlogPostsIntegrationTest extends BaseIntegrationTest
{
    private static final String BASIC_POST_NAME        = "Sample - How To Post";
    public static final long BLOG_POST_ID = 6522541212L;

    private HubspotBlogPostClient hubspotClient;

    @Before
    public void init()
    {
        final Configuration configuration = Configuration.build(clientId, clientSecret, redirectUri, refreshToken);
        hubspotClient = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getBlogPostClient();
    }

    @Test
    public void shouldReturnBlogPost() throws Exception
    {
        BlogPostDetail blogPost = hubspotClient.getBlogPostById(BLOG_POST_ID);

        assertThat(blogPost.getId()).isEqualTo(BLOG_POST_ID);
        assertThat(blogPost.getName()).isEqualTo(BASIC_POST_NAME);
    }

    @Test
    public void shouldListBlogPosts() throws Exception
    {
        BlogPostDetails blogPostDetails = hubspotClient.listBlogPosts(0, 1);
        assertThat(blogPostDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(blogPostDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();

        List<BlogPostDetail> detailList = blogPostDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").hasSize(1);

        BlogPostDetail blogPost = detailList.get(0);
        assertThat(blogPost.getId()).isEqualTo(BLOG_POST_ID);
        assertThat(blogPost.getName()).isEqualTo(BASIC_POST_NAME);
    }

    @Test
    public void shouldListBlogPostsFilterByName() throws Exception
    {
        BlogPostDetails blogPostDetails = hubspotClient.listBlogPosts(0, 1, createSearchFilter(null, BASIC_POST_NAME, false, null, null, null));

        assertThat(blogPostDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(blogPostDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();

        List<BlogPostDetail> detailList = blogPostDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").hasSize(1);

        BlogPostDetail blogPost = detailList.get(0);
        assertThat(blogPost.getId()).isEqualTo(BLOG_POST_ID);
        assertThat(blogPost.getName()).isEqualTo(BASIC_POST_NAME);
    }

    @Test(expected = HubspotApiException.class)
    public void shouldThrowExceptionIfAuthorizationFailed() throws HubspotApiException
    {
        final Configuration configuration = Configuration.build("wrong-client-id", "wrong-client-secret", "wrong-redirect-uri", "wrong-token");
        HubspotBlogPostClient hubspotClient = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getBlogPostClient();
        hubspotClient.listBlogPosts(0, 1);
    }

    private BlogPostFilter createSearchFilter(Long blogId, String name, Boolean archived, String campaign, String slug, BlogPostFilter.State state) {
        BlogPostFilter filter = new BlogPostFilter();
        filter.setBlogId(blogId);
        filter.setPostName(name);
        filter.setArchived(archived);
        filter.setCampaign(campaign);
        filter.setSlug(slug);
        filter.setState(state);
        return filter;
    }
}