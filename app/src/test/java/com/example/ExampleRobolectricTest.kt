package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.ZeroSumEngine
import com.example.model.MoveDirection
import com.example.model.Tile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("نقطه صفر", appName)
  }

  @Test
  fun `test quantum annihilation of equal opposite tiles`() {
    // Left swipe: +16 and -16 in same row should annihilate to 0
    val grid = List(4) { r ->
      List(4) { c ->
        if (r == 0 && c == 0) Tile(id = 1L, value = 16, row = 0, col = 0)
        else if (r == 0 && c == 1) Tile(id = 2L, value = -16, row = 0, col = 1)
        else null
      }
    }

    val result = ZeroSumEngine.executeMove(grid, MoveDirection.LEFT)
    assertTrue("Move should have occurred", result.moved)
    assertEquals("One annihilation event expected", 1, result.annihilations.size)
    assertEquals("Both tiles should have disappeared", null, result.newGrid[0][0])
  }

  @Test
  fun `test fusion of identical sign tiles`() {
    // Left swipe: +2 and +2 should fuse to +4
    val grid = List(4) { r ->
      List(4) { c ->
        if (r == 0 && c == 0) Tile(id = 1L, value = 2, row = 0, col = 0)
        else if (r == 0 && c == 1) Tile(id = 2L, value = 2, row = 0, col = 1)
        else null
      }
    }

    val result = ZeroSumEngine.executeMove(grid, MoveDirection.LEFT)
    assertTrue(result.moved)
    assertEquals(1, result.fusionCount)
    assertEquals(4, result.newGrid[0][0]?.value)
  }

  @Test
  fun `test reduction of unequal opposite tiles`() {
    // Left swipe: +8 and -4 should reduce to +4
    val grid = List(4) { r ->
      List(4) { c ->
        if (r == 0 && c == 0) Tile(id = 1L, value = 8, row = 0, col = 0)
        else if (r == 0 && c == 1) Tile(id = 2L, value = -4, row = 0, col = 1)
        else null
      }
    }

    val result = ZeroSumEngine.executeMove(grid, MoveDirection.LEFT)
    assertTrue(result.moved)
    assertEquals(1, result.reductionCount)
    assertEquals(4, result.newGrid[0][0]?.value)
  }
}

