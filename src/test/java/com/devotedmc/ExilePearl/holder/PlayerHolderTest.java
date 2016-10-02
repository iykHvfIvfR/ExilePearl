package com.devotedmc.ExilePearl.holder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.devotedmc.ExilePearl.PlayerProvider;
import com.devotedmc.ExilePearl.core.MockPearl;

public class PlayerHolderTest {
	
	private Player player;
	private Location loc;
	private PlayerHolder holder;

	@Before
	public void setUp() throws Exception {
		
		World w = mock(World.class);
		when(w.getName()).thenReturn("world");
		
		loc = new Location(w, 0, 1, 2);
		player = mock(Player.class);
		when(player.getLocation()).thenReturn(loc);
		when(player.getName()).thenReturn("Player");
		
		holder = new PlayerHolder(player);
	}

	@Test
	public void testBlockHolder() {
		// Null arguments throw exceptions
		Throwable e = null;
		try { new PlayerHolder(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
	}

	@Test
	public void testGetName() {
		assertEquals(holder.getName(), "Player");
	}

	@Test
	public void testGetLocation() {
		assertEquals(holder.getLocation(), loc);
	}

	@Test
	public void testValidate() {
		MockPearl pearl = new MockPearl(mock(PlayerProvider.class), UUID.randomUUID(), UUID.randomUUID(), loc);
		final ItemStack pearlStack = pearl.createItemStack();
		StringBuilder sb = new StringBuilder();
		
		assertEquals(holder.validate(pearl, sb), HolderVerifyResult.PLAYER_NOT_ONLINE);
		
		when(player.isOnline()).thenReturn(true);
		ItemStack cursorItem = mock(ItemStack.class);
		when(player.getItemOnCursor()).thenReturn(cursorItem);
		
		PlayerInventory inv = mock(PlayerInventory.class);
		when(player.getInventory()).thenReturn(inv);
		
		assertEquals(holder.validate(pearl, sb), HolderVerifyResult.DEFAULT);

		HashMap<Integer, ItemStack> invItems = new HashMap<Integer, ItemStack>();
		invItems.put(0, pearlStack);
		when(inv.all(Material.ENDER_PEARL)).thenAnswer(new Answer<HashMap<Integer, ItemStack>>() {

			@Override
			public HashMap<Integer, ItemStack> answer(InvocationOnMock invocation) throws Throwable {
				return invItems;
			}
		});
		
		assertEquals(holder.validate(pearl, sb), HolderVerifyResult.IN_CHEST);
		
		when(player.getItemOnCursor()).thenReturn(pearlStack);
		assertEquals(holder.validate(pearl, sb), HolderVerifyResult.IN_HAND);
	}
}